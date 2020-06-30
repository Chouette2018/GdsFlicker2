package com.exodia.gdsk.gdsflicker.ui.gallery


import android.app.SearchManager
import android.content.Context
import android.content.res.Configuration
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.*
import android.view.View.INVISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.GridLayout.VERTICAL
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.exodia.gdsk.gdsflicker.R
import com.exodia.gdsk.gdsflicker.data.PhotoItem
import com.exodia.gdsk.gdsflicker.data.Scenario
import com.exodia.gdsk.gdsflicker.data.Status
import com.exodia.gdsk.gdsflicker.databinding.FragmentPhotoGalleryBinding
import com.exodia.gdsk.gdsflicker.polling.PollWorker
import com.exodia.gdsk.gdsflicker.search.QueryPreferences
import com.exodia.gdsk.gdsflicker.ui.VisibleFragment
import com.google.android.material.snackbar.Snackbar


class PhotoGalleryFragment : VisibleFragment() {
    private val TAG = PhotoGalleryFragment::class.java.simpleName
    private lateinit var photoGalleryViewModel: PhotoGalleryViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PhotosAdapter
    private lateinit var progressBar : ProgressBar
    private var suggestions: MutableList<String> = arrayListOf()
    private lateinit var gridLayoutManager : StaggeredGridLayoutManager
    private val NUM_COL_LANDSCAPE = 3
    private val NUM_COL_PORTRAIT = 2
    private lateinit var mListener:OrientationEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        photoGalleryViewModel = ViewModelProvider(this).get(PhotoGalleryViewModel::class.java)

        retainInstance = true
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentPhotoGalleryBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_photo_gallery, container,false )

        progressBar = binding.pbLoadingContent

        recyclerView = binding.rvPhotoGallery

        val orientation = resources.configuration.orientation
        gridLayoutManager = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            StaggeredGridLayoutManager(NUM_COL_LANDSCAPE, VERTICAL)
        } else {
            // In portrait
            StaggeredGridLayoutManager(NUM_COL_PORTRAIT, VERTICAL)
        }
        recyclerView.layoutManager = gridLayoutManager

        adapter = PhotosAdapter()
        recyclerView.adapter =  adapter

        //remove last viewed Image Title on the bar if necessary
        setSubtitle(photoGalleryViewModel.searchTerm)

        val mListener = object : OrientationEventListener(context)
        {
            override fun onOrientationChanged(angle: Int) {
                when (angle) {
                    in 0..45 -> {
                        Log.d(TAG, "gdsportrait")
                        gridLayoutManager.spanCount = NUM_COL_PORTRAIT
                    }
                    in 46..135 -> {
                        gridLayoutManager.spanCount = NUM_COL_LANDSCAPE
                    }
                    in 136..225 -> {
                        Log.d(TAG, "gdsportrait")
                        gridLayoutManager.spanCount = NUM_COL_PORTRAIT
                    }
                    in 226..315 -> {
                        gridLayoutManager.spanCount = NUM_COL_LANDSCAPE
                    }
                    in 316..360 -> {
                        Log.d(TAG, "gdsportrait")
                        gridLayoutManager.spanCount = NUM_COL_PORTRAIT
                    }
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photoGalleryViewModel.getPhotosForContext().observe(
            viewLifecycleOwner,
            Observer { photoItems: PagedList<PhotoItem> ->
                photoItems.let {
                    Log.d(TAG, "photos are ready to be displayed")
                    adapter.submitList(it)
                }
            }
        )

        photoGalleryViewModel.getSearchTerms().observe(
            viewLifecycleOwner,
            Observer {userQueries ->
                userQueries?.let {
                    suggestions.clear()
                    suggestions.addAll(it)
                }
            }
        )

        photoGalleryViewModel.dataLoadingState.observe(
            viewLifecycleOwner,
            Observer {networkState ->
                networkState?.let {
                    when(networkState.status){
                        Status.LOADING ->{
                            if(networkState.scenario == Scenario.INITIAL) {
                                Log.d(TAG, "network data loading")
                                progressBar.visibility = View.VISIBLE
                                recyclerView.visibility = INVISIBLE
                            }
                        }
                        Status.SUCCESS ->{
                            Log.d(TAG, "network data loaded")
                            progressBar.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                        }
                        Status.FAILED ->{
                            Log.d(TAG, "network error")
                            progressBar.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                            displayErrorMessage(networkState.msg)
                        }
                    }
                }
            }
        )
    }

    private fun displayErrorMessage(errorMessage :String?){
        if(errorMessage != null) {
            Snackbar.make(recyclerView, errorMessage, Snackbar.LENGTH_LONG)
                .setAction("OK", object : View.OnClickListener {
                    override fun onClick(view: View?) {
                    }
                })
                .setActionTextColor(resources.getColor(R.color.primaryTextColor))
                .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.photo_gallery_menu,menu)

        val searchMenuItem:MenuItem = menu.findItem(R.id.menu_item_search)
        val searchView = searchMenuItem.actionView as SearchView

        //to avoid the keyboard in full screen mode when in landscape mode
        searchView.imeOptions = searchView.imeOptions or EditorInfo.IME_FLAG_NO_EXTRACT_UI

        val columnNames  = arrayOf( SearchManager.SUGGEST_COLUMN_TEXT_1 )
        val viewIds = intArrayOf(R.id.tv_query_text)
        val cursorAdapter = SimpleCursorAdapter(context,
            R.layout.query_list_item, null, columnNames, viewIds, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)
        searchView.suggestionsAdapter = cursorAdapter

        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(queryText: String?): Boolean {
                    Log.d(TAG, "text submitted : $queryText")
                    if(queryText != null && !queryText.isEmpty()) {
                        updateSearch(queryText)
                        return true
                    }
                    return false
                }

                override fun onQueryTextChange(queryText: String?): Boolean {
                    Log.d(TAG, "text changed : $queryText")

                    val cursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
                    queryText?.let {
                        suggestions.forEachIndexed { index, suggestion ->
                            if (suggestion.contains(queryText, true))
                                cursor.addRow(arrayOf(index, suggestion))
                            //cursor
                        }
                    }
                    cursorAdapter.changeCursor(cursor)
                    return false
                }
            })

            setOnSearchClickListener {
                setQuery(photoGalleryViewModel.searchTerm, false)
            }

            setOnSuggestionListener(object: SearchView.OnSuggestionListener {
                override fun onSuggestionSelect(position: Int): Boolean {
                    return false
                }

                override fun onSuggestionClick(position: Int): Boolean {
                    val cursor = suggestionsAdapter.getItem(position) as Cursor
                    val selection = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
                    setQuery(selection, false)

                    updateSearch(selection)
                    return true
                }
            })

            //collapsing searchview when query is done.
            setOnQueryTextFocusChangeListener { _ , queryTextFocused ->
                if(!queryTextFocused) {
                    onActionViewCollapsed()
                }
            }
        }

        //Polling
        val toggleItem = menu.findItem(R.id.menu_item_poll)
        val isPolling = QueryPreferences.isPolling(requireContext())
        val toggleItemIcon = if(isPolling){
            R.drawable.ic_notifications_off_white_24dp
        }else{
            R.drawable.ic_notifications_active_white_24dp
        }

        toggleItem.setIcon(toggleItemIcon)
        val toggleItemTitle = if(isPolling){
            R.string.stop_polling
        }else{
            R.string.start_polling
        }
        toggleItem.setTitle(toggleItemTitle)
    }

    fun SearchView.updateSearch(query :String) = this.let{
        hideKeyboard()
        clearFocus()
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = INVISIBLE
        refreshSearch(query)
    }

    private fun SearchView.hideKeyboard() = this.let {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_item_refresh -> {
                refreshSearch(photoGalleryViewModel.searchTerm)
                true
            }
            R.id.menu_item_poll -> {
                PollWorker.updatePolling(requireActivity().applicationContext)
                activity?.invalidateOptionsMenu()
                true
            }
            R.id.menu_item_clear -> {
                refreshSearch("")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun refreshSearch(searchTerm : String){
        photoGalleryViewModel.apply{
            searchPhotos(searchTerm)
            setSubtitle(searchTerm)
        }
    }

    private fun setSubtitle(searchTerm : String){
        if(activity is AppCompatActivity) {
            (activity as AppCompatActivity).supportActionBar?.subtitle = searchTerm
        }
    }


}