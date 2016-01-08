package net.technicpack.barcraft.gui.config;

import net.minecraft.client.Minecraft;
import net.technicpack.barcraft.WorldOfBarcraft;
import net.technicpack.barcraft.api.IAction;
import net.technicpack.barcraft.api.IActionContainer;
import net.technicpack.barcraft.api.IActionContainerRegistry;
import net.technicpack.barcraft.gui.BarcraftGuiStats;
import net.technicpack.barcraft.gui.mvc.IGuiModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ModelBarcraftConfig implements IGuiModel, Iterable<IActionContainer> {

    private IActionContainerRegistry containerRegistry = null;
    private IActionContainer selectedBar = null;
    private boolean hasMultipleBars = false;
    private int abilityIndexForPage = 0;
    private int selectedAbility = -1;
    private List<IAction> actionsForBar = new ArrayList<IAction>();
    private BarcraftGuiStats barcraftGuiStats = null;
    private double scrollPos = 0;

    public ModelBarcraftConfig() {
        containerRegistry = WorldOfBarcraft.proxy.getApi().getActionContainerRegistry();

        //We need a handful of things to manage the bar selection: the current selected bar, and
        //we need to know if there's 1 or many bars to select from.  If we have multiple, the left/
        //right buttons will be visible & just loop through the options.  Only one, though, and we'll
        //hide the selection buttons.

        //Only pay attention to action bars with render data that are set to show up in the config gui
        if (containerRegistry != null) {
            Iterator<IActionContainer> container = iterator();
            if (container.hasNext()) {
                this.selectedBar = container.next();
                if (container.hasNext())
                    hasMultipleBars = true;
            }
        }
    }

    @Override
    public void initModel() {
        selectBar(selectedBar);
    }

    private void selectBar(IActionContainer bar) {
        if (this.selectedBar != bar)
            this.selectedAbility = -1;
        this.selectedBar = bar;
        this.actionsForBar.clear();

        if (selectedBar != null) {
            this.barcraftGuiStats = new BarcraftGuiStats(this.selectedBar);

            for (IAction action : WorldOfBarcraft.proxy.getApi().getActionRegistry().getActions()) {
                if (action != null && selectedBar.canAddAction(action) && action.canJoinBar(selectedBar) && WorldOfBarcraft.proxy.getApi().playerHasAction(Minecraft.getMinecraft().thePlayer, action))
                    actionsForBar.add(action);
            }
        }
    }

    public IActionContainer getCurrentBar() { return selectedBar; }
    public boolean hasMultipleBars() { return hasMultipleBars; }
    public int getActionForBarCount() { return actionsForBar.size(); }
    public IAction getCurrentAction() {
        if (selectedAbility < 0 || selectedAbility >= actionsForBar.size())
            return null;
        return actionsForBar.get(selectedAbility);
    }
    public int getActionIndexOnPage() { return selectedAbility - abilityIndexForPage; }
    public int getPageIndex() { return abilityIndexForPage; }
    public IAction getAction(int index) { return actionsForBar.get(index); }
    public BarcraftGuiStats getGuiStats() { return this.barcraftGuiStats; }
    public double getScrollPos() { return scrollPos; }

    public void selectNextBar() {
        Iterator<IActionContainer> iterator = this.iterator();

        IActionContainer current = iterator.next();
        //Loop through valid bars until you find the currently-selected one
        //then grab the very next]
        while (current != null) {
            if (current == selectedBar) {
                IActionContainer next = iterator.next();
                //If we were already on the last bar (or something went wrong), grab the very first bar
                if (next == null)
                    selectFirstBar();
                else
                    selectBar(next);
                return;
            }
            current = iterator.next();
        }

        selectFirstBar();
    }

    public void selectPrevBar() {
        Iterator<IActionContainer> iterator = this.iterator();
        IActionContainer current = iterator.next();

        //Loop through valid bars until you find the currently-selected one
        //then grab the previous
        IActionContainer previous = null;
        while (current != null) {
            if (current == selectedBar)
                break;
            previous = current;
            current = iterator.next();
        }

        //If we were already at the first action bar (or something went wrong), select the very last bar
        if (previous == null)
            selectLastBar();
        else
            selectBar(previous);
    }

    //Change selected action bar to the first registered valid action bar
    private void selectFirstBar() {
        Iterator<IActionContainer> iterator = this.iterator();
        selectBar(iterator.next());
    }

    //Change selected action bar to the last registered valid action bar
    private void selectLastBar() {
        Iterator<IActionContainer> iterator = this.iterator();

        //Get the final valid bar in the list
        IActionContainer current = iterator.next();
        IActionContainer last = null;
        while (current != null) {
            last = current;
            current = iterator.next();
        }
        selectBar(last);
    }

    public void setActionPage(int index) {
        this.abilityIndexForPage = index;
        if (this.abilityIndexForPage < 0)
            this.abilityIndexForPage = 0;
        if (this.abilityIndexForPage >= this.actionsForBar.size())
            this.abilityIndexForPage = this.actionsForBar.size() - 1;
    }

    public void selectAbility(int index) {
        this.selectedAbility = index;
    }

    class ValidBarsIterator implements Iterator<IActionContainer> {
        private Iterator<IActionContainer> innerIterator;
        private IActionContainer next = null;

        public ValidBarsIterator(Iterator<IActionContainer> innerIterator) {
            this.innerIterator = innerIterator;
            next = getNextValidBar(innerIterator);
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public IActionContainer next() {
            IActionContainer next = this.next;
            this.next = getNextValidBar(innerIterator);
            return next;
        }

        @Override
        public void remove() {

        }

        /// Given an action container iterator, pulls the next container that belongs in this menu
        private IActionContainer getNextValidBar(Iterator<IActionContainer> iterator) {
            while (iterator.hasNext()) {
                IActionContainer testBar = iterator.next();
                if (testBar.getRenderData() != null && testBar.getRenderData().appearsInMenu()) {
                    return testBar;
                }
            }

            return null;
        }
    }

    @Override
    public Iterator<IActionContainer> iterator() {
        return new ValidBarsIterator(containerRegistry.getActionBars().iterator());
    }
}
