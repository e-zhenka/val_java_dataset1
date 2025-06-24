@Override
        public Long next()
        {
            if (currentNumber >= maxValue)
            {
                throw new NoSuchElementException();
            }
            if (currentNumber < currentEnd)
            {
                return currentNumber++;
            }
            currentNumber = start[++currentRange];
            currentEnd = end[currentRange];
            return currentNumber++;
        }