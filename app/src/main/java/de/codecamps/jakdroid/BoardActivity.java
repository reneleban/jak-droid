package de.codecamps.jakdroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import de.codecamps.jakdroid.auth.AccountGeneral;

import java.util.ArrayList;
import java.util.List;

public class BoardActivity extends AppCompatActivity {
    private BoardActivity boardActivityReference;
    private String authToken = null;
    private DrawerLayout mDrawerLayout;
    private String activeBoardId;

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_board);

        this.boardActivityReference = this;
        authToken = getSharedPreferences(AccountGeneral.ACCOUNT_TYPE, Context.MODE_PRIVATE).getString(AccountGeneral.ACCOUNT_NAME, null);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        // initialize drawer navigation
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        new UpdateBoardList(this).execute();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Set behavior of Navigation drawer
        NavigationView.OnNavigationItemSelectedListener navigationItemListener = new BoardOnNavigationItemSelectedListener(toolbar);
        navigationView.setNavigationItemSelectedListener(navigationItemListener);

        // add new card button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getActiveBoardId() == null) {
                    Snackbar.make(getCurrentFocus(), getString(R.string.no_list_chosen), Snackbar.LENGTH_LONG).show();
                } else {
                    showNewCardDialog();
                }
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list, menu);
        return true;
    }

    /**
     * Fragment Adapter for ViewPager
     */
    class Adapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        void removeAllFragments() {
            mFragmentList.clear();
            mFragmentTitleList.clear();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        public void removePosition(int pos) {
            mFragmentTitleList.remove(pos);
            mFragmentList.remove(pos);
        }
    }

    // add new list, open drawer (offcanvas)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_list_toolbar:
                ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

                BoardActivity.Adapter adapter = ((Adapter) ((ViewPager) findViewById(R.id.viewpager)).getAdapter());

                Fragment fragment = adapter.getItem(viewPager.getCurrentItem());
                Bundle b = fragment.getArguments();
                final String listId = b.getString("list_id");
                adapter.removePosition(viewPager.getCurrentItem());
                adapter.notifyDataSetChanged();

                new DeleteList(authToken).execute(listId);

                return super.onOptionsItemSelected(item);
            case R.id.add_new_list_toolbar:
                if (getActiveBoardId() == null) {
                    Snackbar.make(getCurrentFocus(), getString(R.string.no_board_chosen), Snackbar.LENGTH_LONG).show();
                } else {
                    showNewListDialog();
                }
                return super.onOptionsItemSelected(item);
            case android.R.id.home: // show side menu, no break cause of super-call
                mDrawerLayout.openDrawer(GravityCompat.START);
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private class BoardOnNavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {
        private final Toolbar toolbar;

        BoardOnNavigationItemSelectedListener(Toolbar toolbar) {
            this.toolbar = toolbar;
        }

        // select board and load lists
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.add_new_board) {
                showNewBoardDialog();
            } else {

                /**
                 * TODO: find better uncheck solution!
                 */
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                SubMenu boards = navigationView.getMenu().findItem(0).getSubMenu();
                for (int i = 0; i < boards.size(); i++) {
                    boards.getItem(i).setChecked(false);
                }

                // Set item in checked state
                menuItem.setChecked(true);
                Intent intent = menuItem.getIntent();
                if (intent != null) {
                    String boardId = intent.getStringExtra("board_id");
                    toolbar.setTitle(intent.getStringExtra("board_name"));
                    if (boardId != null && !boardId.isEmpty()) {
                        activeBoardId = boardId;
                        Log.d(AccountGeneral.ACCOUNT_NAME, String.format("Called item with uuid: %s", boardId));
                        new UpdateListElements(boardActivityReference).execute(boardId); // load board lists and update views!
                    }
                }
            }
            mDrawerLayout.closeDrawers();
            return true;
        }
    }

    private void showNewCardDialog() {
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        final BoardActivity.Adapter adapter = ((Adapter) viewPager.getAdapter());
        Fragment fragment = adapter.getItem(tabs.getSelectedTabPosition());
        Bundle b = fragment.getArguments();
        final String listId = b.getString("list_id");

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_new_card, null);
        dialogBuilder.setView(dialogView);
        final EditText edt = (EditText) dialogView.findViewById(R.id.new_card_title);
        dialogBuilder.setTitle(getString(R.string.add_new_card));
        dialogBuilder.setMessage(getString(R.string.add_new_card_name));
        dialogBuilder.setPositiveButton(getString(R.string.add_new_card_button_positive), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                RecyclerView recyclerView = (RecyclerView) (adapter.getItem(viewPager.getCurrentItem())).getView();
                new AddNewCard(getAuthToken(), recyclerView).execute(listId, edt.getText().toString());
            }
        });
        dialogBuilder.setNegativeButton(getString(R.string.add_new_card_button_negative), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                edt.setText("");
            }
        });
        AlertDialog ad = dialogBuilder.create();
        ad.show();
    }

    private void showNewBoardDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_new_board, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.new_board_title);

        dialogBuilder.setTitle(getString(R.string.add_new_board));
        dialogBuilder.setMessage(getString(R.string.add_new_board_name));
        dialogBuilder.setPositiveButton(getString(R.string.add_new_board_button_positive), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                new AddNewBoard(BoardActivity.this).execute(edt.getText().toString());
            }
        });
        dialogBuilder.setNegativeButton(getString(R.string.add_new_board_button_negative), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                edt.setText("");
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void showNewListDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_new_list, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.new_list_title);

        dialogBuilder.setTitle(getString(R.string.add_new_list));
        dialogBuilder.setMessage(getString(R.string.add_new_list_name));
        dialogBuilder.setPositiveButton(getString(R.string.add_new_list_button_positive), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                new AddNewList(BoardActivity.this).execute(edt.getText().toString());
            }
        });
        dialogBuilder.setNegativeButton(getString(R.string.add_new_list_button_negative), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                edt.setText("");
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    String getActiveBoardId() {
        return activeBoardId;
    }

    String getAuthToken() {
        return authToken;
    }
}
