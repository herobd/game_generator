/*
    Copyright (C) 2013 Stephan Schiffel <stephan.schiffel@gmx.de>

    This file is part of GameController.

    GameController is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GameController is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GameController.  If not, see <http://www.gnu.org/licenses/>.
*/

package tud.gamecontroller;

import java.io.File;
import java.io.IOException;

import tud.gamecontroller.game.ReasonerInterface;
import tud.gamecontroller.game.impl.Game;
import tud.gamecontroller.term.TermFactoryInterface;
import tud.gamecontroller.term.TermInterface;

public interface ReasonerFactoryInterface<TermType extends TermInterface<TermType>, ReasonerStateInfoType> {

	public ReasonerInterface<TermType, ReasonerStateInfoType> createReasoner(String gameDescription, String gameName);

	public TermFactoryInterface<TermType> getTermFactory();

	public Game<TermType, ReasonerStateInfoType> createGameFromFile(File gameFile, GDLVersion gdlVersion) throws IOException;

	public Game<TermType, ReasonerStateInfoType> createGameFromFile(File gameFile, GDLVersion gdlVersion, String stylesheet) throws IOException;
	
	public Game<TermType, ReasonerStateInfoType> createGameFromFile(File gameFile, GDLVersion gdlVersion, String stylesheet, File sightFile) throws IOException;

	public Game<TermType, ReasonerStateInfoType> createGame(String gameDescription, String name, GDLVersion gdlVersion);
	
	public Game<TermType, ReasonerStateInfoType> createGame(String gameDescription, String name, GDLVersion gdlVersion, String stylesheet);
	
	public Game<TermType, ReasonerStateInfoType> createGame(String gameDescription, String name, GDLVersion gdlVersion, String stylesheet, String seesXMLRules);
	
}