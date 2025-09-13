package com.davidp.chessjourney.application.domain;



public class OpenTacticGameEvent {

    String tacticGameId = null;

    public  OpenTacticGameEvent(){

    }

    public OpenTacticGameEvent(String tacticGameId) {
        this.tacticGameId = tacticGameId;
    }

    public String getTacticGameId() {
        return tacticGameId;
    }

    public boolean isTacticGameIdValid(){
        return tacticGameId != null && !tacticGameId.isEmpty();
    }
}
