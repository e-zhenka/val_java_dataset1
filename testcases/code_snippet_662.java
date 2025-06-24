private void renewSeedInternal() {
        String currentSeed = this.seed;
        String newSeed = currentSeed;
        byte[] bytes = new byte[SEED_NUM_BYTES];
        while (Objects.equals(newSeed, currentSeed)) {
            RANDOM.nextBytes(bytes);
            newSeed = new String(Hex.encodeHex(bytes));
        }
        this.seed = newSeed;
    }