package forge.screens.home;

import forge.Forge;
import forge.assets.FImage;
import forge.assets.FSkinImage;
import forge.menu.FMenuItem;
import forge.menu.FPopupMenu;
import forge.model.FModel;
import forge.properties.ForgePreferences;
import forge.properties.ForgePreferences.FPref;
import forge.screens.FScreen;
import forge.screens.constructed.ConstructedScreen;
import forge.screens.gauntlet.NewGauntletScreen;
import forge.screens.home.puzzle.PuzzleScreen;
import forge.screens.limited.NewDraftScreen;
import forge.screens.limited.NewSealedScreen;
import forge.screens.planarconquest.NewConquestScreen;
import forge.screens.quest.NewQuestScreen;
import forge.toolbox.FEvent;
import forge.toolbox.FEvent.FEventHandler;
import forge.util.Localizer;

public class NewGameMenu extends FPopupMenu {
    final static Localizer localizer = Localizer.getInstance();

    public enum NewGameScreen {
        Constructed(localizer.getMessage("lblConstructed"), FSkinImage.DECKLIST, ConstructedScreen.class),
        BoosterDraft(localizer.getMessage("lblBoosterDraft"), FSkinImage.HAND, NewDraftScreen.class),
        SealedDeck(localizer.getMessage("lblSealedDeck"), FSkinImage.PACK, NewSealedScreen.class),
        QuestMode(localizer.getMessage("lblQuestMode"), FSkinImage.QUEST_ZEP, NewQuestScreen.class),
        PuzzleMode(localizer.getMessage("lblPuzzleMode"), FSkinImage.QUEST_BOOK, PuzzleScreen.class),
        PlanarConquest(localizer.getMessage("lblPlanarConquest"), FSkinImage.MULTIVERSE, NewConquestScreen.class),
        Gauntlet(localizer.getMessage("lblGauntlet"), FSkinImage.ALPHASTRIKE, NewGauntletScreen.class);

        private final FMenuItem item;
        private final Class<? extends FScreen> screenClass;
        private FScreen screen;

        NewGameScreen(final String caption0, final FImage icon0, final Class<? extends FScreen> screenClass0) {
            screenClass = screenClass0;
            item = new FMenuItem(caption0, icon0, new FEventHandler() {
                @Override
                public void handleEvent(FEvent e) {
                    open(true); //remove current screen from chain
                    setPreferredScreen(NewGameScreen.this);
                }
            });
        }
        
        private void initializeScreen() {
            if (screen == null) { //don't initialize screen until it's opened the first time
                try {
                    screen = screenClass.newInstance();
                    screen.setHeaderCaption(localizer.getMessage("lblNewGame") + " - " + item.getText());
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

        public void open() {
            open(false);
        }
        private void open(boolean replaceBackScreen) {
            initializeScreen();
            Forge.openScreen(screen, replaceBackScreen);
        }

        public void setAsBackScreen(boolean replace) {
            initializeScreen();
            Forge.setBackScreen(screen, replace);
        }
    }

    private static final ForgePreferences prefs = FModel.getPreferences();
    private static final NewGameMenu menu = new NewGameMenu();
    private static NewGameScreen preferredScreen;

    static {
        try {
            preferredScreen = NewGameScreen.valueOf(prefs.getPref(FPref.NEW_GAME_SCREEN));
        }
        catch (Exception ex) {
            ex.printStackTrace();
            preferredScreen = NewGameScreen.Constructed;
            prefs.setPref(FPref.NEW_GAME_SCREEN, preferredScreen.name());
            prefs.save();
        }
    }

    public static NewGameScreen getPreferredScreen() {
        return preferredScreen;
    }
    public static void setPreferredScreen(NewGameScreen preferredScreen0) {
        if (preferredScreen == preferredScreen0) { return; }
        preferredScreen = preferredScreen0;
        prefs.setPref(FPref.NEW_GAME_SCREEN, preferredScreen.name());
        prefs.save();
    }

    public static NewGameMenu getMenu() {
        return menu;
    }

    private NewGameMenu() {
    }

    @Override
    protected void buildMenu() {
        FScreen currentScreen = Forge.getCurrentScreen();
        for (NewGameScreen ngs : NewGameScreen.values()) {
            addItem(ngs.item);
            ngs.item.setSelected(currentScreen == ngs.screen);
        }
    }
}
