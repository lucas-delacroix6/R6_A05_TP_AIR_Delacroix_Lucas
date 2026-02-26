package com.example.blockchain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;

public class Block {
    public int index;
    public String timestamp;
    public String data;
    public String previousHash;
    public String hash;

    public String eventId;
    public String artist;
    public Status status;
    public String ticketOwner;

    public int nonce; // Pour le Proof of Work

    public Block(int index, String data, String previousHash, String eventId, String artist, Status status, String ticketOwner) {
        this.index = index;
        this.timestamp = Instant.now().toString();
        this.data = data;
        this.previousHash = previousHash;
        this.eventId = eventId;
        this.artist = artist;
        this.status = status;
        this.ticketOwner = ticketOwner;
        this.nonce = 0;
        this.hash = calculateHash();
    }

    public String calculateHash() {
        try {
            String input = index + timestamp + data + previousHash + eventId + artist + status + ticketOwner + nonce;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Bloc Miné ! Hash: " + hash);
    }
}