package game.constructs.pieces

import generator.CrossOver
import generator.FineTunable
import generator.Gene

class StartingPosition implements FineTunable, Gene
{
	public static enum PositionType {
        HomeRow, HomeCorner, OppRow, OppCorner, Center
    }
    
    private static final List<PositionType> VALUES =
        Collections.unmodifiableList(Arrays.asList(PositionType.values())) as List<PositionType>
    private static final int SIZE = VALUES.size();

    private PositionType type = PositionType.HomeRow
    private int number = 0
    
    StartingPosition() {
        type = VALUES.get(RANDOM.nextInt(SIZE))
        number = (int) (Math.round(RANDOM.nextGaussian() * 4) + 4)
        if (number<0)
            number=0
    }
    
    StartingPosition(int number) {
        type = VALUES.get(RANDOM.nextInt(SIZE))
        this.number = number
    }
    
    StartingPosition(PositionType type) {
        this.type = type
        number = (int) (Math.round(RANDOM.nextGaussian() * 4) + 4)
        if (number<0)
            number=0
    }
    
    StartingPosition(PositionType type, int number) {
        this.type = type
        this.number = number
    }
    
    StartingPosition(String type, int number) {
        this.type = type
        this.number = number
    }

    int getNumber()
    {
        return number
    }

    PositionType getType()
    {
        return type
    }

    @Override
    int getNumParams()
	{
	    return 1
	}
	
	@Override
    void changeParam(int param, int amount)
    {
        number+=amount;
    }

	@Override
	List<CrossOver> getPossibleCrossOvers(Gene other)
	{
		other = other as StartingPosition
		def result = []

		def c = {StartingPosition sp -> this.type = sp.type}
		c.curry(other)
		result.add(new CrossOver(c))

		c = {StartingPosition sp -> this.number = sp.number}
		c.curry(other)
		result.add(new CrossOver(c))

		return result
	}

	@Override
	String toString()
	{
		return "Starting Position: " + convertToJSON()
	}
    
    String convertToJSON()
    {
        return '{"type":"'+type+'", "number":'+number+'}'
    }
    
    static StartingPosition fromJSON(def parsed)
    {
        return new StartingPosition(parsed.type, parsed.number)
    }   
}

