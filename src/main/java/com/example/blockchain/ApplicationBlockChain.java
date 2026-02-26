package com.example.blockchain;

public class ApplicationBlockChain {
    public static void main(String[] args) {
        Blockchain ticketChain = new Blockchain();

        // 1. Achat initial
        ticketChain.addBlock("Achat Initial", "CONCERT-2024", "Daft Punk", Status.ACHETE, "Alice");

        // 2. Revente 1
        ticketChain.addBlock("Revente n°1", "CONCERT-2024", "Daft Punk", Status.REVENU, "Bob");

        // 3. Revente 2
        ticketChain.addBlock("Revente n°2", "CONCERT-2024", "Daft Punk", Status.REVENU, "Charlie");

        // 4. Utilisation
        ticketChain.addBlock("Entrée au concert", "CONCERT-2024", "Daft Punk", Status.UTILISE, "Charlie");

        // Vérification
        System.out.println("\nLa blockchain est-elle valide ? " + ticketChain.isChainValid());

        // Export JSON
        ticketChain.exportAsJson();
    }
}