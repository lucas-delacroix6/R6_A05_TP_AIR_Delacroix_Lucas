#!/usr/bin/env bash
# =============================================================================
# MasterAnnonce – Script de test de charge simple (curl)
# TP Dev Avancé #4 – IUT Montreuil BUT 3
#
# Usage :
#   chmod +x load-test.sh
#   ./load-test.sh                          # défaut : 50 itérations, 5 concurrents
#   ./load-test.sh 100 10                   # 100 itérations, 10 concurrents
#   BASE_URL=http://host:8080/app/api ./load-test.sh
# =============================================================================

set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080/api}"
ITERATIONS="${1:-50}"
CONCURRENCY="${2:-5}"
RESULTS_FILE="load-test-results.csv"

# Couleurs
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${GREEN}╔══════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║  MasterAnnonce – Test de charge                 ║${NC}"
echo -e "${GREEN}╠══════════════════════════════════════════════════╣${NC}"
echo -e "${GREEN}║  URL       : ${BASE_URL}${NC}"
echo -e "${GREEN}║  Itérations: ${ITERATIONS}${NC}"
echo -e "${GREEN}║  Concurrence: ${CONCURRENCY}${NC}"
echo -e "${GREEN}╚══════════════════════════════════════════════════╝${NC}"
echo ""

# Fichier CSV résultats
echo "test,iteration,http_code,time_total_s,time_connect_s,time_starttransfer_s,size_download" > "$RESULTS_FILE"

# ─── Helpers ─────────────────────────────────────────────────────────────────

run_test() {
    local label="$1"
    local method="$2"
    local url="$3"
    local data="${4:-}"
    local token="${5:-}"
    local iteration="$6"

    local header_args=""
    if [ -n "$token" ]; then
        header_args="-H \"Authorization: Bearer $token\""
    fi
    if [ -n "$data" ]; then
        header_args="$header_args -H \"Content-Type: application/json\" -d '$data'"
    fi

    eval curl -s -o /dev/null -w "\"%{http_code},%{time_total},%{time_connect},%{time_starttransfer},%{size_download}\"" \
        -X "$method" $header_args "$url" | while IFS=',' read -r code total connect start size; do
        echo "$label,$iteration,$code,$total,$connect,$start,$size" >> "$RESULTS_FILE"
    done
}

login() {
    local token
    token=$(curl -s -X POST "$BASE_URL/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"username":"john","password":"password"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    echo "$token"
}

# ─── Phase 1 : Healthcheck ──────────────────────────────────────────────────

echo -e "${YELLOW}[Phase 1] Healthcheck...${NC}"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/annonces?page=0&size=1" || echo "000")
if [ "$HTTP_CODE" != "200" ]; then
    echo -e "${RED}ERREUR : Le serveur ne répond pas ($HTTP_CODE). Vérifiez que Tomcat tourne.${NC}"
    exit 1
fi
echo -e "${GREEN}  ✔ Serveur accessible (200)${NC}"

# ─── Phase 2 : Obtention du token ───────────────────────────────────────────

echo -e "${YELLOW}[Phase 2] Authentification...${NC}"
TOKEN=$(login)
if [ -z "$TOKEN" ]; then
    echo -e "${RED}ERREUR : Login échoué. Vérifiez que JAAS est configuré et la BDD peuplée.${NC}"
    exit 1
fi
echo -e "${GREEN}  ✔ Token obtenu${NC}"

# ─── Phase 3 : Tests de charge ──────────────────────────────────────────────

echo -e "${YELLOW}[Phase 3] Lancement des tests ($ITERATIONS itérations × $CONCURRENCY concurrents)...${NC}"
echo ""

# Test 1 : GET /annonces (lecture paginée)
echo -e "  ${YELLOW}→ GET /annonces (lecture paginée)${NC}"
START=$(date +%s%N)
seq 1 "$ITERATIONS" | xargs -P "$CONCURRENCY" -I {} bash -c "
    curl -s -o /dev/null -w '{},GET_annonces,%{http_code},%{time_total},%{time_connect},%{time_starttransfer},%{size_download}\n' \
    '$BASE_URL/annonces?page=0&size=10' >> '$RESULTS_FILE'
"
END=$(date +%s%N)
ELAPSED=$(( (END - START) / 1000000 ))
echo -e "  ${GREEN}✔ Terminé en ${ELAPSED}ms${NC}"

# Test 2 : GET /annonces/1 (lecture détail)
echo -e "  ${YELLOW}→ GET /annonces/1 (détail)${NC}"
START=$(date +%s%N)
seq 1 "$ITERATIONS" | xargs -P "$CONCURRENCY" -I {} bash -c "
    curl -s -o /dev/null -w '{},GET_annonces_1,%{http_code},%{time_total},%{time_connect},%{time_starttransfer},%{size_download}\n' \
    '$BASE_URL/annonces/1' >> '$RESULTS_FILE'
"
END=$(date +%s%N)
ELAPSED=$(( (END - START) / 1000000 ))
echo -e "  ${GREEN}✔ Terminé en ${ELAPSED}ms${NC}"

# Test 3 : POST /annonces (écriture avec auth)
echo -e "  ${YELLOW}→ POST /annonces (création avec auth)${NC}"
START=$(date +%s%N)
seq 1 "$ITERATIONS" | xargs -P "$CONCURRENCY" -I {} bash -c "
    curl -s -o /dev/null -w '{},POST_annonces,%{http_code},%{time_total},%{time_connect},%{time_starttransfer},%{size_download}\n' \
    -X POST \
    -H 'Content-Type: application/json' \
    -H 'Authorization: Bearer $TOKEN' \
    -d '{\"title\":\"Load test {}\",\"description\":\"Annonce créée par le test de charge\",\"price\":99.99,\"category\":\"Electronique\"}' \
    '$BASE_URL/annonces' >> '$RESULTS_FILE'
"
END=$(date +%s%N)
ELAPSED=$(( (END - START) / 1000000 ))
echo -e "  ${GREEN}✔ Terminé en ${ELAPSED}ms${NC}"

# Test 4 : POST /login (authentification répétée)
echo -e "  ${YELLOW}→ POST /login (auth répétée)${NC}"
START=$(date +%s%N)
seq 1 "$ITERATIONS" | xargs -P "$CONCURRENCY" -I {} bash -c "
    curl -s -o /dev/null -w '{},POST_login,%{http_code},%{time_total},%{time_connect},%{time_starttransfer},%{size_download}\n' \
    -X POST \
    -H 'Content-Type: application/json' \
    -d '{\"username\":\"john\",\"password\":\"password\"}' \
    '$BASE_URL/auth/login' >> '$RESULTS_FILE'
"
END=$(date +%s%N)
ELAPSED=$(( (END - START) / 1000000 ))
echo -e "  ${GREEN}✔ Terminé en ${ELAPSED}ms${NC}"

# ─── Phase 4 : Rapport ──────────────────────────────────────────────────────

echo ""
echo -e "${GREEN}═══════════════════════════════════════════════════${NC}"
echo -e "${GREEN}  RAPPORT DE RÉSULTATS${NC}"
echo -e "${GREEN}═══════════════════════════════════════════════════${NC}"
echo ""

# Parse CSV et grouper par test
for TEST_NAME in GET_annonces GET_annonces_1 POST_annonces POST_login; do
    LINES=$(grep "$TEST_NAME" "$RESULTS_FILE" 2>/dev/null | grep -v "^test," || true)
    COUNT=$(echo "$LINES" | grep -c . || echo 0)

    if [ "$COUNT" -gt 0 ]; then
        SUCCESS=$(echo "$LINES" | awk -F',' '$3 >= 200 && $3 < 400' | wc -l)
        ERRORS=$(echo "$LINES" | awk -F',' '$3 >= 400' | wc -l)
        AVG_TIME=$(echo "$LINES" | awk -F',' '{sum += $4; n++} END {if(n>0) printf "%.3f", sum/n; else print "N/A"}')
        MIN_TIME=$(echo "$LINES" | awk -F',' 'NR==1 || $4 < min {min=$4} END {printf "%.3f", min}')
        MAX_TIME=$(echo "$LINES" | awk -F',' 'NR==1 || $4 > max {max=$4} END {printf "%.3f", max}')

        echo -e "  ${YELLOW}$TEST_NAME${NC}"
        echo -e "    Requêtes : $COUNT | ✔ $SUCCESS | ✗ $ERRORS"
        echo -e "    Temps (s): avg=$AVG_TIME  min=$MIN_TIME  max=$MAX_TIME"
        echo ""
    fi
done

echo -e "${GREEN}Résultats détaillés dans: $RESULTS_FILE${NC}"
echo -e "${GREEN}Vous pouvez importer ce CSV dans un tableur pour analyse.${NC}"
