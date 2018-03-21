package com.frederikam.robotchess.audio;

import com.frederikam.robotchess.chess.pieces.*;

import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public abstract class ChessLocale {

    abstract String voiceLocale();

    abstract List<String> getKeywords();

    static Class<? extends ChessPiece> interpretChessPiece(String s) {
        switch (s) {
            case "pawn":
            case "bonde":
                return Pawn.class;
            case "rook":
            case "tower":
            case "tårn":
                return Rook.class;
            case "bishop":
            case "biskop":
                return Bishop.class;
            case "springer":
            case "knight":
            case "horse":
            case "ridder":
            case "hest":
                return Knight.class;
            case "king":
            case "konge":
                return King.class;
            case "queen":
            case "dronning":
                return Queen.class;
            default:
                return null;
        }
    }

    public static class English extends ChessLocale {

        @Override
        public String voiceLocale() {
            return "en-US";
        }

        @Override
        public List<String> getKeywords() {
            return List.of(
                    "pawn",
                    "rook",
                    "bishop",
                    "springer",
                    "knight",
                    "king",
                    "queen"
            );
        }
    }

    public static class Danish extends ChessLocale {

        @Override
        public String voiceLocale() {
            return "da-DK";
        }

        @Override
        public List<String> getKeywords() {
            return List.of(
                    "bonde",
                    "tårn",
                    "biskop",
                    "ridder",
                    "hest",
                    "konge",
                    "dronning"
            );
        }
    }

}
