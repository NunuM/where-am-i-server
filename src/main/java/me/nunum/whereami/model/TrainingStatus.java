package me.nunum.whereami.model;

public enum TrainingStatus {
    REQUEST {
        @Override
        public String toString() {
            return "request";
        }
    },
    PROGRESS {
        @Override
        public String toString() {
            return "in progress";
        }
    },
    FINISHED {
        @Override
        public String toString() {
            return "finished";
        }
    }
}
