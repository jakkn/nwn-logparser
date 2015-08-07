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
