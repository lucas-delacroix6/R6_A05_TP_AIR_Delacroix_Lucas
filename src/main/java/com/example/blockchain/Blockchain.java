package com.example.blockchain;

import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Blockchain {
    private final List<Block> chain;
    private final int difficulty = 4; // Pour le PoW

    public Blockchain() {
        chain = new ArrayList<>();
        Block genesis = new Block(0, "Genesis Block", "0", "EVT-000", "System", Status.INITIAL, "None");
        genesis.mineBlock(difficulty);
        chain.add(genesis);
    }

    public void addBlock(String data, String eventId, String artist, Status status, String owner) {
        Block lastBlock = chain.get(chain.size() - 1);
        Block newBlock = new Block(chain.size(), data, lastBlock.hash, eventId, artist, status, owner);

        newBlock.mineBlock(difficulty);

        chain.add(newBlock);
    }

    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("Erreur: Hash actuel invalide au bloc " + i);
                return false;
            }
            if (!currentBlock.previousHash.equals(previousBlock.hash)) {
                System.out.println("Erreur: Le chaînage est rompu au bloc " + i);
                return false;
            }
        }
        return true;
    }

    public void exportAsJson() {
        String json = new GsonBuilder().setPrettyPrinting().create().toJson(chain);
        try (FileWriter writer = new FileWriter("blockchain.json")) {
            writer.write(json);
            System.out.println("Blockchain exportée dans blockchain.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Block> getChain() { return chain; }
}