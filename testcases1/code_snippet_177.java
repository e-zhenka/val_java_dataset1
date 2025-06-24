@Override
        public Long next()
        {
            if (currentNumber < currentEnd)
            {
                return currentNumber++;
            }
            currentNumber = start[++currentRange];
            currentEnd = end[currentRange];
            return currentNumber++;
        }