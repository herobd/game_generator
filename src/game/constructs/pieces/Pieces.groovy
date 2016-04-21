package game.constructs.pieces

import generator.CrossOver
import generator.FineTunable
import generator.Gene
import generator.Mutation
import generator.NestedCrossOver

/**
 * @author Lawrence Thatcher
 *
 * A wrapper class for holding multiple Piece objects
 */
class Pieces implements Iterable<Piece>, FineTunable, Gene
{
	private List<Piece> pieces

	Pieces(List<Piece> pieces)
	{
		this.pieces = pieces
	}
	
	Pieces(int numP)
	{
		this.pieces = []
		for (int i=0; i<numP; i++)
		{
		    this.pieces.push(new Piece())
		}
	}

	List<Piece> getPieces()
	{
		return pieces
	}

	void namePieces()
	{
		for (int i=0; i<pieces.size(); i++)
		{
			this.pieces[i].setName('p'+i)
		}
	}

	@Override
	Iterator<Piece> iterator()
	{
		return pieces.iterator()
	}

	@Override
	int getNumParams()
	{
		int result = 0
		for (Piece p : pieces)
			result += p.numParams
		return result
	}

	@Override
	void changeParam(int param, int amount)
	{
		int i = 0
		for (Piece p : pieces)
		{
			if (param >= i +p.numParams)
				i += p.numParams
			else
				p.changeParam(param-i, amount)
		}
	}

	@Override
	List<Mutation> getPossibleMutations()
	{
		def result = []
		for (Piece p : pieces)
			result.addAll(p.possibleMutations)
			
		return result
	}

	@Override
	List<CrossOver> getPossibleCrossOvers(Gene other)
	{
		other = other as Pieces
		def result = []
		result.add(new NestedCrossOver(this.pieces, other.pieces))
		def c = {Pieces ps -> this.pieces = ps.pieces}
		c.curry(other)
		result.add(new CrossOver(c))
		return result
	}
}
