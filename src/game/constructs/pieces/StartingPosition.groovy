package game.constructs.pieces

import generator.FineTunable

class StartingPosition implements FineTunable
{
    public static enum PositionType {
        HomeRow, HomeCorner, OppRow, OppCorner, Center
    }
    
    private static final List<PositionType> VALUES =
        Collections.unmodifiableList(Arrays.asList(PositionType.values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    private PositionType type=PositionType.HomeRow
    private int number=0
    
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
    
    String convertToJSON()
    {
        return '{"type":"'+type+'", "number":'+number+'}'
    }
    
    static StartingPosition fromJSON(def parsed)
    {
        return new StartingPosition(parsed.type, parsed.number)
    }   
}

