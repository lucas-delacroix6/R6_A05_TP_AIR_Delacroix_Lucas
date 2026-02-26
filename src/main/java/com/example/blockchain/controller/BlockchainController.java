package com.example.blockchain.controller;

import com.example.blockchain.model.Block;
import com.example.blockchain.service.BlockchainService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blockchain")
public class BlockchainController {

    private final BlockchainService service;

    public BlockchainController(BlockchainService service) {
        this.service = service;
    }

    @GetMapping("/chain")
    public List<Block> getChain() {
        return service.getChain();
    }

    @PostMapping("/mine")
    public Block addBlock(@RequestParam(defaultValue = "pow") String consensus,
                          @RequestBody Map<String, String> payload) {

        String data = payload.get("data");
        String eventId = payload.get("eventId");
        String artist = payload.get("artist");
        String status = payload.get("status");
        String owner = payload.get("owner");

        return switch (consensus.toLowerCase()) {
            case "pos" -> service.addBlockPoS(data, eventId, artist, status, owner);
            case "pbft" -> service.addBlockPBFT(data, eventId, artist, status, owner);
            case "poa" -> service.addBlockPoA(data, eventId, artist, status, owner, "Admin1");
            default -> service.addBlockPoW(data, eventId, artist, status, owner);
        };
    }

    @GetMapping("/validate")
    public String validateChain() {
        return service.isChainValid() ? "La blockchain est VALIDE" : "La blockchain est CORROMPUE";
    }

    @GetMapping("/export")
    public String exportChain() {
        return service.exportAsJson();
    }

    @PostMapping("/simulate-workflow")
    public String simulateTicketWorkflow() {
        service.addBlockPoW("Création du billet", "FEST-2025", "Coldplay", "ACHETE", "Alice");
        service.addBlockPoS("Revente du billet sur la plateforme", "FEST-2025", "Coldplay", "REVENU", "Bob");
        service.addBlockPBFT("Revente de dernière minute", "FEST-2025", "Coldplay", "REVENU", "Charlie");
        service.addBlockPoA("Scan à l'entrée du festival", "FEST-2025", "Coldplay", "UTILISE", "Charlie", "Admin1");

        return "Workflow simulé avec succès ! Faites un GET sur /chain pour voir l'historique.";
    }
}