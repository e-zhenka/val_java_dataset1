private void renewSeedInternal() {
        String currentSeed = this.seed;
        String newSeed = currentSeed;
        while (Objects.equals(newSeed, currentSeed)) {
            newSeed = new String(Hex.encodeHex(RANDOM.generateSeed(SEED_NUM_BYTES)));
        }
        this.seed = newSeed;
    }