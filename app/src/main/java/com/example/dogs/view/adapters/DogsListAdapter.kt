package com.example.dogs.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.dogs.R
import com.example.dogs.databinding.ItemDogBinding
import com.example.dogs.model.entities.DogBreed
import com.example.dogs.utils.loadImage
import com.example.dogs.view.DogClickListener
import com.example.dogs.view.fragments.ListFragmentDirections
import kotlinx.android.synthetic.main.item_dog.view.*

class DogsListAdapter (private val dogsList: ArrayList<DogBreed>):
    RecyclerView.Adapter<DogsListAdapter.DogViewHolder>(),
    DogClickListener {

    fun updateDogList(newDogsList: List<DogBreed>) {
        dogsList.clear()
        dogsList.addAll(newDogsList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<ItemDogBinding>(inflater, R.layout.item_dog, parent, false)
        return DogViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dogsList.size
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        holder.view.dog = dogsList[position]
        holder.view.listener = this
        // Solução antiga. Holder guardava uma view
        /* val dog = dogsList[position]
        holder.apply {
            dog.run {
                tvName.text = dogBreed
                tvLifeSpan.text = lifeSpan
                imageView.loadImage(imageUrl!!)
            }
            view.root.setOnClickListener {
                val action = ListFragmentDirections.actionDetailFragment()
                action.dogUuid = dog.uuid
                Navigation.findNavController(it).navigate(action)
            }
        }*/
    }

    class DogViewHolder(val view: ItemDogBinding) : RecyclerView.ViewHolder(view.root)

    override fun onDogClicked(view: View, uuid: Int) {
        val action = ListFragmentDirections.actionDetailFragment()
        action.dogUuid = uuid
        Navigation.findNavController(view).navigate(action)
    }
}
