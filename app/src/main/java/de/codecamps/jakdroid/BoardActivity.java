package de.codecamps.jakdroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.*;
import android.widget.EditText;
import de.codecamps.jakdroid.auth.AccountGeneral;

import java.util.ArrayList;
import java.util.List;

import java.util.UUID;


// https://codelabs.developers.google.com/codelabs/material-design-style/index.html?index=..%2F..%2Findex#7
public class BoardActivity extends AppCompatActivity {
    private BoardActivity boardActivity;
    private String authToken = null;
    private DrawerLayout mDrawerLayout;

    public String getActive_board_id() {
        return active_board_id;
    }

    private String active_board_id;

    public String getAuthToken() {
        return authToken;
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public void removeAllFragments() {
            mFragmentList.clear();
            mFragmentTitleList.clear();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
//        // TODO: retrieve initial board and lists
//        for (int i : new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9}) {
//            Bundle arguments = new Bundle();
//            arguments.putString("uuid", String.valueOf(UUID.randomUUID()));
//            CardContentFragment cardContentFragment = new CardContentFragment();
//            cardContentFragment.setArguments(arguments);
//            adapter.addFragment(cardContentFragment, "Card " + i);
//        }
        viewPager.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete_board_toolbar:
                // TODO Implement Delete current item load first board
                return super.onOptionsItemSelected(item);
            case R.id.add_new_list_toolbar:
                if(getActive_board_id() == null){
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_board);

        this.boardActivity = this;
        authToken = getSharedPreferences(AccountGeneral.ACCOUNT_TYPE, Context.MODE_PRIVATE).getString(AccountGeneral.ACCOUNT_NAME, null);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);



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
        NavigationView.OnNavigationItemSelectedListener navigationItemListener = new NavigationView.OnNavigationItemSelectedListener() {
            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.add_new_board) {
                    Log.d(AccountGeneral.ACCOUNT_NAME, "add new board clicked");
                    showNewBoardDialog();
                } else {
                    // Set item in checked state
                    menuItem.setChecked(true);
                    Intent intent = menuItem.getIntent();
                    if (intent != null) {
                        String board_id = intent.getStringExtra("board_id");
                        String board_name = intent.getStringExtra("board_name");
                        toolbar.setTitle(board_name);
                        if (board_id != null && !board_id.isEmpty()) {
                            active_board_id = board_id;
                            Log.d(AccountGeneral.ACCOUNT_NAME, String.format("Called item with uuid: %s", board_id));
                            // TODO: load board lists and update views!
                            new UpdateListElements(boardActivity).execute(board_id);
                        }
                    }
                }
                // TODO: handle navigation
                // Closing drawer on item click
                mDrawerLayout.closeDrawers();
                return true;
            }
        };

        navigationView.setNavigationItemSelectedListener(
                navigationItemListener);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(getActive_board_id() == null){
                    Snackbar.make(getCurrentFocus(), getString(R.string.no_list_chosen), Snackbar.LENGTH_LONG).show();
                } else {
                    TabLayout tabs = (TabLayout) findViewById(R.id.tabs);

                    BoardActivity.Adapter adapter = ((Adapter) ((ViewPager) findViewById(R.id.viewpager)).getAdapter());
                    Fragment fragment = adapter.getItem(tabs.getSelectedTabPosition());
                    Bundle b = fragment.getArguments();
                    String list_id = b.getString("uuid");

                    Snackbar.make(v, "TODO: add new card to list: " + list_id,
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    public void showNewBoardDialog() {
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

    public void showNewListDialog() {
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


}
