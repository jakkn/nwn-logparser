/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logParser;

/**
 *
 * @author jakob
 */
public interface ModelInterface {
    void initialize();
    void run();
    void pause();
    void unpause();
    void registerObserver(ParserObserver o);
    void removeObserver(ParserObserver o);
}
