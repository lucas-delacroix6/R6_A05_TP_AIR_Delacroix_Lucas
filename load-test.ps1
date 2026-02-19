# =============================================================================
# MasterAnnonce – Script de test de charge simple (PowerShell / curl)
# TP Dev Avancé #3 – IUT Montreuil BUT 3
#
# Usage :
#   .\load-test.ps1                          # défaut : 50 itérations
#   .\load-test.ps1 -Iterations 100
#   .\load-test.ps1 -BaseUrl "http://host:8080/app/api"
# =============================================================================

param(
    [string]$BaseUrl = "http://localhost:8080/masterannonce/api",
    [int]$Iterations = 50
)

$ResultsFile = "load-test-results.csv"

Write-Host ""
Write-Host "╔══════════════════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║  MasterAnnonce – Test de charge (PowerShell)    ║" -ForegroundColor Green
Write-Host "╠══════════════════════════════════════════════════╣" -ForegroundColor Green
Write-Host "║  URL        : $BaseUrl" -ForegroundColor Green
Write-Host "║  Itérations : $Iterations" -ForegroundColor Green
Write-Host "╚══════════════════════════════════════════════════╝" -ForegroundColor Green
Write-Host ""

# CSV header
"test,iteration,http_code,time_ms" | Out-File -FilePath $ResultsFile -Encoding UTF8

# ─── Helpers ─────────────────────────────────────────────────────────────────

function Invoke-LoadTest {
    param(
        [string]$Label,
        [string]$Method,
        [string]$Url,
        [string]$Body = $null,
        [hashtable]$Headers = @{}
    )

    Write-Host "  → $Label" -ForegroundColor Yellow
    $results = @()
    $sw = [System.Diagnostics.Stopwatch]::StartNew()

    for ($i = 1; $i -le $Iterations; $i++) {
        $iterSw = [System.Diagnostics.Stopwatch]::StartNew()
        try {
            $params = @{
                Method = $Method
                Uri = $Url
                Headers = $Headers
                ContentType = "application/json"
                ErrorAction = "SilentlyContinue"
            }
            if ($Body) { $params.Body = $Body }

            $response = Invoke-WebRequest @params -UseBasicParsing
            $code = $response.StatusCode
        }
        catch {
            if ($_.Exception.Response) {
                $code = [int]$_.Exception.Response.StatusCode
            } else {
                $code = 0
            }
        }
        $iterSw.Stop()
        "$Label,$i,$code,$($iterSw.ElapsedMilliseconds)" | Out-File -FilePath $ResultsFile -Append -Encoding UTF8
        $results += [PSCustomObject]@{ Code = $code; TimeMs = $iterSw.ElapsedMilliseconds }
    }

    $sw.Stop()
    $success = ($results | Where-Object { $_.Code -ge 200 -and $_.Code -lt 400 }).Count
    $errors = ($results | Where-Object { $_.Code -ge 400 -or $_.Code -eq 0 }).Count
    $avgMs = [math]::Round(($results | Measure-Object -Property TimeMs -Average).Average, 1)
    $minMs = ($results | Measure-Object -Property TimeMs -Minimum).Minimum
    $maxMs = ($results | Measure-Object -Property TimeMs -Maximum).Maximum

    Write-Host "    Requêtes: $($results.Count) | OK: $success | Erreurs: $errors" -ForegroundColor Cyan
    Write-Host "    Temps (ms): avg=$avgMs  min=$minMs  max=$maxMs" -ForegroundColor Cyan
    Write-Host "    Total: $($sw.ElapsedMilliseconds)ms" -ForegroundColor Green
    Write-Host ""
}

# ─── Phase 1 : Healthcheck ──────────────────────────────────────────────────

Write-Host "[Phase 1] Healthcheck..." -ForegroundColor Yellow
try {
    $health = Invoke-WebRequest -Uri "$BaseUrl/helloWorld" -UseBasicParsing
    if ($health.StatusCode -eq 200) {
        Write-Host "  ✔ Serveur accessible (200)" -ForegroundColor Green
    }
} catch {
    Write-Host "  ✗ Le serveur ne répond pas. Vérifiez que Tomcat tourne." -ForegroundColor Red
    exit 1
}
Write-Host ""

# ─── Phase 2 : Authentification ─────────────────────────────────────────────

Write-Host "[Phase 2] Authentification..." -ForegroundColor Yellow
try {
    $loginBody = '{"username":"john","password":"password"}'
    $loginResp = Invoke-RestMethod -Uri "$BaseUrl/login" -Method POST -ContentType "application/json" -Body $loginBody
    $Token = $loginResp.token
    Write-Host "  ✔ Token obtenu" -ForegroundColor Green
} catch {
    Write-Host "  ✗ Login échoué. Vérifiez JAAS et la BDD." -ForegroundColor Red
    exit 1
}
Write-Host ""

# ─── Phase 3 : Tests de charge ──────────────────────────────────────────────

Write-Host "[Phase 3] Lancement des $Iterations itérations..." -ForegroundColor Yellow
Write-Host ""

$authHeaders = @{ "Authorization" = "Bearer $Token" }

# Test 1 : GET /annonces
Invoke-LoadTest -Label "GET_annonces" -Method "GET" -Url "$BaseUrl/annonces?page=0&size=10"

# Test 2 : GET /annonces/1
Invoke-LoadTest -Label "GET_annonces_1" -Method "GET" -Url "$BaseUrl/annonces/1"

# Test 3 : POST /annonces (avec auth)
$createBody = '{"title":"Load test PS","description":"Annonce test de charge PowerShell","price":99.99,"category":"Test"}'
Invoke-LoadTest -Label "POST_annonces" -Method "POST" -Url "$BaseUrl/annonces" -Body $createBody -Headers $authHeaders

# Test 4 : POST /login (auth répétée)
Invoke-LoadTest -Label "POST_login" -Method "POST" -Url "$BaseUrl/login" -Body '{"username":"john","password":"password"}'

# ─── Rapport ─────────────────────────────────────────────────────────────────

Write-Host "═══════════════════════════════════════════════════" -ForegroundColor Green
Write-Host "Résultats détaillés dans : $ResultsFile" -ForegroundColor Green
Write-Host "Importez ce CSV dans Excel/Sheets pour analyse." -ForegroundColor Green
