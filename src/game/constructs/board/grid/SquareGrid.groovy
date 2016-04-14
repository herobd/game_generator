package game.constructs.board.grid

import game.constructs.condition.functions.SupportsInARow
import game.constructs.condition.functions.SupportsOpen
import game.gdl.clauses.GDLClause
//import game.gdl.clauses.HasClausesWithDep
import game.gdl.clauses.base.BaseClause
import game.gdl.clauses.base.HasBaseClause
import game.gdl.clauses.dynamic.DynamicComponentsClause
import game.gdl.clauses.init.HasInitClause
import game.gdl.clauses.init.InitClause
import game.gdl.statement.SimpleStatement
import game.constructs.pieces.Piece
import game.constructs.pieces.StartingPosition
import game.constructs.player.Players
import generator.CrossOver
import generator.Gene
import generator.Mutation

/**
 * @author Lawrence Thatcher
 *
 * A square grid with square tiles.
 * TODO: support different x,y sizes
 */
class SquareGrid extends Grid implements
		HasBaseClause,
		HasInitClause,
		SupportsInARow,
		SupportsOpen,
		Gene//HasClauses
{
	private int size
	private boolean i_nbors = false

	public SquareGrid(int size)
	{
		this.size = size
	}

	public SquareGrid(int size, boolean i_nbors)
	{
		this.size = size
		this.i_nbors = i_nbors
	}

	@Override
	Collection<GDLClause> getGDLClauses(List<Piece> pieces, Players players)
	{
		def clauses = []
		clauses.add(hasCellsClause)
		clauses.add(generateIndexClause())
		clauses.add(generateSuccessorClause())
		clauses.add(generateInitClause(pieces,players))
		return clauses
	}

	@Override
	BaseClause getBaseAndInputClause()
	{
		def result = (BaseClause)generateIndexClause()
		result.join(generateSuccessorClause())
		return result
	}

	@Override
	InitClause getInitialStateClause()
	{
		return (InitClause)generateInitClause()
	}

	@Override
	GDLClause in_a_row(List n)
	{
		n = n as List<Integer>
		String name = Integer.toString(n[0]) + "inARow"

		def s = []
		s.add(line_row(n[0]))
		s.add(line_column(n[0]))
		if (this.i_nbors)
		{
			s.add(line_diag_desc(n[0]))
			s.add(line_diag_asc(n[0]))
		}
		s.add(new SimpleStatement("(<= (" + name + " ?w) (row${n[0]} ?x ?y ?w))"))
		s.add(new SimpleStatement("(<= (" + name + " ?w) (column${n[0]} ?x ?y ?w))"))
		if (this.i_nbors)
			s.add(new SimpleStatement("(<= (" + name + " ?w) (diagonal${n[0]} ?x ?y ?w))"))

		return new DynamicComponentsClause(s)
	}

	@Override
	GDLClause open()
	{
		SimpleStatement s = new SimpleStatement("(<= open\n(true (cell ?x ?y b)))")
		return new DynamicComponentsClause([s])
	}

	@Override
	SquareGrid clone()
	{
		return new SquareGrid(this.size, this.i_nbors)
	}

	protected GDLClause generateIndexClause()
	{
		def indices = []
		for (int i = 1; i <= this.size; i++)
		{
			SimpleStatement s = new SimpleStatement("(index " + Integer.toString(i) + ")")
			indices.add(s)
		}
		return new BaseClause(indices)
	}

	protected GDLClause generateSuccessorClause()
	{
		def succs = []
		for (int i = 1; i < this.size; i++)
		{
			SimpleStatement s = new SimpleStatement("(succ " + Integer.toString(i) + " " + Integer.toString(i+1) +  ")")
			succs.add(s)
		}
		return new BaseClause(succs)
	}

	protected GDLClause generateInitClause(List<Piece> pieces, Players players)
	{
		def cells = []
		
		List< List<String> > setup=[]
		for (int i = 1; i <= this.size; i++)
		{
		    List<String> setupr=[]
			for (int j = 1; j <= this.size; j++)
			{
			    setupr.push('b')
			}
			setup.push(setupr);
		}
		/* HomeRow
		 **************
		 *      1     *0
		 *            *1
		 *3          4*.
		 *            *.
		 *      2     *i
		 **************
		  0 1 ...    j
		 */
		 /* HomeCorner
		 **************
		 *1          4*0
		 *            *1
		 *            *.
		 *            *.
		 *3          2*i
		 **************
		  0 1 ...    j
		 */
		int center = Math.floor(size/2)
		for (Piece p : pieces)
		{
		    for (StartingPosition sp : p.getStartPositions())
		    {
		        
	            List<String> pNames = players.getPlayerNames();
	            for (int n=0; n<sp.number; n++)
	            {
	                for (int playerIndex=0; playerIndex<pNames.size(); playerIndex++)
	                {
	                    int curI=-1
                        int curJ=-1
	                    if (sp.type==StartingPosition.PositionType.HomeRow || sp.type==StartingPosition.PositionType.OppRow)
	                    {
	                        int index=playerIndex%4
                            if (sp.type==StartingPosition.PositionType.OppRow)
                                if (index%2==0)
                                    index++
                                else
                                    index--
	                        int offInc=1
	                        int offSwitch=-1
	                        int curA=(index==0||index==2)?0:this.size-1
                            int curB=center
                            if (index==0 || index==1) {
                                 curI=curA
                                 curJ=curB
                            }
                            else
                            {
                                 curI=curB
                                 curJ=curA
                            }
                            
                            while (setup[curI][curJ]!='b')//find the next blank
                            {
                                
                                curB+=offSwitch*(offInc++)
                                offSwitch*=-1
                                
                                if (curB>this.size-1 || curB<0)
                                {
                                    curB=center
                                    curA+=(index==0||index==2)?1:-1
                                    if (curA>this.size-1 || curA<0) 
                                        break
                                    offInc=1
                                    offSwitch=-1
                                }
                                
                                if (index==0 || index==1) {
	                                 curI=curA
	                                 curJ=curB
                                }
                                else
                                {
                                     curI=curB
	                                 curJ=curA
                                }
                                
                                if (curI<0 || curI>this.size-1 ||
                                curJ<0 || curJ>this.size-1)
                                    break
                            }
	                        
	                        
                           
                            
                        }
                        else if (sp.type==StartingPosition.PositionType.HomeCorner || sp.type==StartingPosition.PositionType.OppCorner)
                        {
                            int index=playerIndex%4
                            if (sp.type==StartingPosition.PositionType.OppCorner)
                                if (index%2==0)
                                    index++
                                else
                                    index--
                            int shell=0
                            int offI=0
                            int offJ=0
                            if (index==0)
                            {
                                curI=0
                                curJ=0
                            }
                            else if (index==1)
                            {
                                curI=this.size-1
                                curJ=this.size-1
                            }
                            else if (index==3)
                            {
                                curI=0
                                curJ=this.size-1
                            }
                            else if (index==2)
                            {
                                curI=this.size-1
                                curJ=0
                            }
                            while (setup[curI][curJ]!='b')
                            {
                                if (offI==0)
                                {
                                    offI=++shell
                                    offJ=0
                                }
                                else
                                {
                                    offI--
                                    offJ++
                                }
                                
                                if (index==0)
                                {
                                    curI=offI
                                    curJ=offJ
                                }
                                else if (index==1)
                                {
                                    curI=(this.size-1) -offI
                                    curJ=(this.size-1) -offJ
                                }
                                else if (index==3)
                                {
                                    curI=offI
                                    curJ=(this.size-1) -offJ
                                }
                                else if (index==2)
                                {
                                    curI=(this.size-1) -offI
                                    curJ=offJ
                                }
                                
                                if (curI<0 || curI>this.size-1 ||
                                    curJ<0 || curJ>this.size-1)
                                    break
                            }
                            
                            
                        }
                        else if (sp.type==StartingPosition.PositionType.Center)
                        {
                            int shell=0
                            int offI=0
                            int offJ=-1
                            curI=center
                            curJ=center
                            while (setup[curI][curJ]!='b') {
                                if (offI==shell && offJ==shell-1)
                                {
                                    shell++
                                    offI=shell
                                    offJ=shell
                                }
                                else if (offJ==shell && offI>-shell)
                                    offI--
                                else if (offI==-shell && offJ>-shell)
                                    offJ--
                                else if (offJ==-shell && offI<shell)
                                    offI++
                                else if (offI==shell)
                                    offJ++
                                
                                curI=center-offI
                                curJ=center-offJ
                                
                                if (curI<0 || curI>this.size-1 ||
                                    curJ<0 || curJ>this.size-1)
                                    break
                            }
                        }
                        
                        if (curI>=0 && curI<this.size &&
                            curJ>=0 && curJ<this.size)
                        {
                            setup[curI][curJ]=p.getName(pNames[playerIndex]);
                        }
	                }
                }
		        
		    }
		}
		
		for (int i = 0; i < this.size; i++)
		{
			for (int j = 0; j < this.size; j++)
			{
				
				SimpleStatement s = new SimpleStatement("(init (cell " + Integer.toString(i+1) + " " + Integer.toString(j+1) + " "+setup[i][j]+"))")
				cells.add(s)
			}
		}
		return new InitClause(cells)
	}

	private static SimpleStatement line_row(int n)
	{
		return line_gen("row", n, Increment.Ascending, Increment.None)
	}

	private static SimpleStatement line_column(int n)
	{
		return line_gen("column",n, Increment.None, Increment.Ascending)
	}

	private static SimpleStatement line_diag_desc(int n)
	{
		return line_gen("diagonal", n, Increment.Ascending, Increment.Ascending)
	}

	private static SimpleStatement line_diag_asc(int n)
	{
		return line_gen("diagonal", n, Increment.Ascending, Increment.Descending)
	}

	private static SimpleStatement line_gen(String name, int n, Increment xInc, Increment yInc)
	{
		String result = ""
		result += "(<= (${name + Integer.toString(n)} ?x ?y ?w)\n"
		def x = new Index("x")
		if (xInc == Increment.Descending)
			x = new Index("x", n-1)
		def y = new Index("y")
		if (yInc == Increment.Descending)
			y = new Index("y", n-1)
		String xSucc = ""
		String ySucc = ""
		String s = ""
		for (int i = 1; i <= n; i++)
		{
			if (x.value > 0)
			{
				xSucc += "(succ ${x-1} ${x})\n"
			}
			if (y.value > 0)
			{
				ySucc += "(succ ${y-1} ${y})\n"
			}
			s += "(true (cell  ${x} ${y} ?w))"
			if (i < n)
				s += "\n"
			xInc(x)
			yInc(y)
		}
		result += xSucc
		result += ySucc
		result += s
		result += ")\n"
		return new SimpleStatement(result)
	}
	
	/*List<List<String>> getSelectedSpaceNborsGDL(int i,Set<GString> definitions)
	{
	    definitions.add("(succ ?selm${i} ?selm${i}H)")
	    definitions.add("(succ ?selm${i}L ?selm${i})")
	    definitions.add("(succ ?seln${i} ?seln${i}H)")
	    definitions.add("(succ ?seln${i}L ?seln${i})")
	    return [["?selm${i}", "?seln${i}H"],
	            ["?selm${i}", "?seln${i}L"],
	            ["?selm${i}H", "?seln${i}"],
	            ["?selm${i}L", "?seln${i}"]]
	}
	List<List<String>> getSelectedSpaceI_NborsGDL(int i,Set<GString> definitions)
	{
	    definitions.add("(succ ?selm${i} ?selm${i}H)")
	    definitions.add("(succ ?selm${i}L ?selm${i})")
	    definitions.add("(succ ?seln${i} ?seln${i}H)")
	    definitions.add("(succ ?seln${i}L ?seln${i})")
	    return [["?selm${i}H', '?seln${i}H"],
	            ["?selm${i}H', '?seln${i}L"],
	            ["?selm${i}L', '?seln${i}H"],
	            ["?selm${i}L', '?seln${i}L"]]
	}*/
	//(nbor ?n ?m ?nn ?nm) 
	//(<= (nbor ?n ?m ?nn ?nm) 
	//    (index ?ALL) (succ ?n ?nn) (?m ?nm))
	
	
	@Override
	int getNumParams()
	{
	    return 1
	}
	
	@Override
    void changeParam(int param, int amount)
    {
        size+=amount
        /*if (param==0)
            size+=amount
        else
            i_nbors=!i_nbors*/
    }

	@Override
	List<Mutation> getPossibleMutations() {
		return null
	}

	@Override
	List<CrossOver> getPossibleCrossOvers(Gene other) {
		return null
	}
}
