package com.davidp.chessjourney.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

/**
 * Domain object representing a tournament.
 */
public class Tournament {
    private String hashId;
    private String provincia;
    private String concejo;
    private String torneo;
    private LocalDate inicio;
    private LocalDate fin;
    private String ritmo;

    public Tournament(String provincia, String concejo, String torneo, LocalDate inicio, LocalDate fin, String ritmo) {
        this.provincia = provincia;
        this.concejo = concejo;
        this.torneo = torneo;
        this.inicio = inicio;
        this.fin = fin;
        this.ritmo = ritmo;
        this.hashId = generateHash();
    }

    public Tournament(String hashId, String provincia, String concejo, String torneo, LocalDate inicio, LocalDate fin, String ritmo) {
        this.hashId = hashId;
        this.provincia = provincia;
        this.concejo = concejo;
        this.torneo = torneo;
        this.inicio = inicio;
        this.fin = fin;
        this.ritmo = ritmo;
    }

    /**
     * Generates a SHA-256 hash from tournament data for deduplication.
     */
    private String generateHash() {
        try {
            String data = provincia + "|" + concejo + "|" + torneo + "|" + inicio + "|" + fin + "|" + ritmo;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    // Getters and setters
    public String getHashId() { return hashId; }
    public String getProvincia() { return provincia; }
    public String getConcejo() { return concejo; }
    public String getTorneo() { return torneo; }
    public LocalDate getInicio() { return inicio; }
    public LocalDate getFin() { return fin; }
    public String getRitmo() { return ritmo; }

    public void setProvincia(String provincia) { this.provincia = provincia; }
    public void setConcejo(String concejo) { this.concejo = concejo; }
    public void setTorneo(String torneo) { this.torneo = torneo; }
    public void setInicio(LocalDate inicio) { this.inicio = inicio; }
    public void setFin(LocalDate fin) { this.fin = fin; }
    public void setRitmo(String ritmo) { this.ritmo = ritmo; }

    @Override
    public String toString() {
        return "Tournament{" +
                "hashId='" + hashId + '\'' +
                ", provincia='" + provincia + '\'' +
                ", concejo='" + concejo + '\'' +
                ", torneo='" + torneo + '\'' +
                ", inicio=" + inicio +
                ", fin=" + fin +
                ", ritmo='" + ritmo + '\'' +
                '}';
    }
}