package info.josefjohn.callermenu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
 
        switch (index) {
        case 0:
            return new LeveledMenuFragment();
        case 1:
            return new CompleteMenuFragment();
        case 2:
        	return new StandardMenuFragment();
        }
 
        return null;
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;
	}
}
