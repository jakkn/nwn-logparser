/*
 * Copyright (C) 2015 jakob
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package logParser;

/**
 *
 * @author jakob
 */
public class ParserController implements ControllerInterface {

    private final ModelInterface model;
    private final ParserView view;
    
    public ParserController(ModelInterface model) {
        this.model = model;
        model.initialize();
        view = new ParserView(this, model);
        view.createView();
    }

    @Override
    public void pause() {
        model.pause();
    }

    @Override
    public void unpause() {
        model.unpause();
    }
    
}
