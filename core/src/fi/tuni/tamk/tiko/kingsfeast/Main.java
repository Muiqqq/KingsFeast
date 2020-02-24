package fi.tuni.tamk.tiko.kingsfeast;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class Main extends ApplicationAdapter {
	// TODO: Add documentation everywhere.

	private final boolean DEBUG_PHYSICS = true;

	// Initial values, work in progress. Pixels -> meters.
	private final float GAME_WIDTH = 800 / 100f;
	private final float GAME_HEIGHT = 450 / 100f;
	private final Vector2 gravity = new Vector2(0, -9.8f);

	private SpriteBatch batch;
	private Texture img;
	private TiledMap tiledMap;
	private TiledMapRenderer tiledMapRenderer;
	private OrthographicCamera camera;
	private World world;
	private Box2DDebugRenderer box2DDebugRenderer;
	private WorldContactListener worldContactListener;

	//PUSHI KOMMENTTI

	@Override
	public void create () {
		batch = new SpriteBatch();
		world = new World(gravity, false);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);

		// tiledMap
		// tiledMapRenderer

		box2DDebugRenderer = new Box2DDebugRenderer();

		worldContactListener = new WorldContactListener();
		world.setContactListener(worldContactListener);

		img = new Texture("badlogic.jpg");
	}

	@Override
	public void render () {
		batch.setProjectionMatrix(camera.combined);
		Util.clearScreen();

		batch.begin();
		batch.draw(img, 0, 0,
				img.getWidth() / 100f,
				img.getHeight() / 100f);

		batch.end();

		if(DEBUG_PHYSICS) {
			box2DDebugRenderer.render(world, camera.combined);
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
