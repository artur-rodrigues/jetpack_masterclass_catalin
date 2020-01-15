package com.example.dogs.view.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dogs.R
import com.example.dogs.utils.gone
import com.example.dogs.utils.visible
import com.example.dogs.view.adapters.DogsListAdapter
import com.example.dogs.viewmodel.ListViewModel
import kotlinx.android.synthetic.main.fragment_list.*

/**
 * A simple [Fragment] subclass.
 */
class ListFragment : Fragment() {

    private lateinit var viewModel: ListViewModel
    private lateinit var dogsListAdapter: DogsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
        viewModel.refresh()

        dogsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = DogsListAdapter(arrayListOf())
                .also {
                dogsListAdapter = it
            }
        }

        refreshLayout.setOnRefreshListener {
            dogsList.gone()
            listError.gone()
            loadingProgress.visible()
            viewModel.refreshBypassCache()
            refreshLayout.isRefreshing = false
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.dogs.observe(this, Observer {dogs ->
            dogs.run {
                dogsList.visible()
                dogsListAdapter.updateDogList(this)
            }
        })

        viewModel.dogsLoadError.observe(this, Observer {isError ->
            isError.run {
                if(this) {
                    listError.visible()
                } else {
                    listError.gone()
                }
            }
        })

        viewModel.loading.observe(this, Observer {isLoading ->
            isLoading.run {
                loadingProgress.visibility = if(this) {
                    listError.gone()
                    dogsList.gone()
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.actionSettings -> {
                view?.let {
                    Navigation.findNavController(it).navigate(ListFragmentDirections.actionListFragmentToSettingsFragment())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
