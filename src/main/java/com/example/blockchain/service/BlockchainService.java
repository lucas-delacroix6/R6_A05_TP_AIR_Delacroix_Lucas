package com.example.blockchain.service;

import com.example.blockchain.model.Block;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Service
public class BlockchainService {
    private final List<Block> chain = new ArrayList<>();
    private final int difficulty = 4;

    private final Map<String, Integer> posValidators = Map.of("Alice", 50, "Bob", 150, "Charlie", 10);
    private final List<String> poaNodes = List.of("Admin1", "Admin2");
    private final List<String> pbftNodes = List.of("NodeA", "NodeB", "NodeC", "NodeD");

    public BlockchainService() {
        Block genesis = new Block(0, "Genesis Block", "0", "EVT-000", "System", "INITIAL", "None");
        genesis.mineBlock(difficulty);
        chain.add(genesis);
    }

    public List<Block> getChain() {
        return chain;
    }

    public Block getLastBlock() {
        return chain.get(chain.size() - 1);
    }

    public Block addBlockPoW(String data, String eventId, String artist, String status, String owner) {
        Block newBlock = new Block(chain.size(), data, getLastBlock().hash, eventId, artist, status, owner);
        newBlock.mineBlock(difficulty);
        chain.add(newBlock);
        return newBlock;
    }

    public Block addBlockPoS(String data, String eventId, String artist, String status, String owner) {
        Block newBlock = new Block(chain.size(), data, getLastBlock().hash, eventId, artist, status, owner);
        String winner = Collections.max(posValidators.entrySet(), Map.Entry.comparingByValue()).getKey();
        newBlock.data += " (Validé par PoS: " + winner + ")";
        chain.add(newBlock);
        return newBlock;
    }

    public Block addBlockPBFT(String data, String eventId, String artist, String status, String owner) {
        Block newBlock = new Block(chain.size(), data, getLastBlock().hash, eventId, artist, status, owner);
        int votesRequired = (int) Math.ceil(pbftNodes.size() * (2.0 / 3.0));

        if (pbftNodes.size() >= votesRequired && newBlock.hash.equals(newBlock.calculateHash())) {
            newBlock.data += " (Validé par PBFT)";
            chain.add(newBlock);
            return newBlock;
        }
        throw new RuntimeException("Consensus PBFT échoué");
    }

    public Block addBlockPoA(String data, String eventId, String artist, String status, String owner, String authorityNode) {
        Block newBlock = new Block(chain.size(), data, getLastBlock().hash, eventId, artist, status, owner);
        if (poaNodes.contains(authorityNode)) {
            newBlock.data += " (Validé par PoA: " + authorityNode + ")";
            chain.add(newBlock);
            return newBlock;
        }
        throw new RuntimeException("Nœud non autorisé pour PoA");
    }


    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block current = chain.get(i);
            Block previous = chain.get(i - 1);
            if (!current.hash.equals(current.calculateHash())) return false;
            if (!current.previousHash.equals(previous.hash)) return false;
        }
        return true;
    }

    public String exportAsJson() {
        String json = new GsonBuilder().setPrettyPrinting().create().toJson(chain);
        try (FileWriter writer = new FileWriter("blockchain.json")) {
            writer.write(json);
            return "Export réussi dans blockchain.json";
        } catch (IOException e) {
            return "Erreur d'export: " + e.getMessage();
        }
    }
}