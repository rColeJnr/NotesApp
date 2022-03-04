package com.notes.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.notes.databinding.ActivityRootBinding
import com.notes.ui._base.FragmentNavigator
import com.notes.ui.list.NoteListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RootActivity : AppCompatActivity(), FragmentNavigator {

    private var viewBinding: ActivityRootBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewBinding = ActivityRootBinding.inflate(layoutInflater)
        this.viewBinding = viewBinding
        setContentView(viewBinding.root)
        // don't create a new instance of fragment if there's already an instance
        /** *replace* removes the existing fragment and adds a new fragment..
         * *add* retains the existing fragments and adds a new fragment, that means existing
         * fragment will be active and they won't be in 'paused' state hence when a back button
         * is pressed onCreateView() is not called for the existing fragment (the fragment which
         * was there before new fragment was added)
         * */
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    viewBinding.container.id,
                    NoteListFragment()
                )
                .commit()
        }
    }

    override fun navigateTo(
        fragment: Fragment
    ) {
        val viewBinding = this.viewBinding ?: return
        // add fragment to back stack
        supportFragmentManager
            .beginTransaction()
            .replace(
                viewBinding.container.id,
                fragment
            )
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

}