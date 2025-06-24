@Override
    protected JsonNode _at(JsonPointer ptr) {
        // 02-Jan-2020, tatu: As per [databind#3003] must return `null` and NOT
        //    "missing node"
        return null;
    }