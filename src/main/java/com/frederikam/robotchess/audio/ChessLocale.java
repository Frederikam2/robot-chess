package com.frederikam.robotchess.audio;

import com.frederikam.robotchess.chess.pieces.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("SpellCheckingInspection")
public abstract class ChessLocale {

    abstract String voiceLocale();

    abstract List<String> getKeywords();

    static Optional<Class<? extends ChessPiece>> interpretChessPiece(String s) {
        switch (s) {
            case "pawn":
            case "bonde":
                return Optional.of(Pawn.class);
            case "rook":
            case "tower":
            case "tårn":
                return Optional.of(Rook.class);
            case "bishop":
            case "biskop":
                return Optional.of(Bishop.class);
            case "springer":
            case "knight":
            case "horse":
            case "ridder":
            case "hest":
                return Optional.of(Knight.class);
            case "king":
            case "konge":
                return Optional.of(King.class);
            case "queen":
            case "dronning":
                return Optional.of(Queen.class);
            default:
                return Optional.empty();
        }
    }

    public static class English extends ChessLocale {

        @Override
        public String voiceLocale() {
            return "en-US";
        }

        @Override
        public List<String> getKeywords() {
            LinkedList<String> list = new LinkedList<>();
            list.add("pawn");
            list.add("rook");
            list.add("bishop");
            list.add("springer");
            list.add("knight");
            list.add("king");
            list.add("queen");

            return list;
        }
    }

    public static class Danish extends ChessLocale {

        @Override
        public String voiceLocale() {
            return "da-DK";
        }

        @Override
        public List<String> getKeywords() {
            LinkedList<String> list = new LinkedList<>();

            list.add("bonde");
            list.add("tårn");
            list.add("biskop");
            list.add("ridder");
            list.add("hest");
            list.add("konge");
            list.add("dronning");

            return list;
        }
    }

}
