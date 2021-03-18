package com.dev_candra.todolistapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dev_candra.todolistapp.databinding.ItemTaskBinding
import com.dev_candra.todolistapp.entity.Task


class TaskAdapter(private val listener: OnItemClickListener) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(DiffCallback()){

   inner class TaskViewHolder(private val binding: ItemTaskBinding): RecyclerView.ViewHolder(binding.root)
    {
        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val task  = getItem(position)
                        listener.onItemClick(task)
                    }
                    checkBox.setOnClickListener {
                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION){
                            val task = getItem(position)
                            listener.onCheckBoxClick(task,checkBox.isChecked)
                        }
                    }
                }
            }
        }

        fun bind(task: Task){
            binding.apply {
                checkBox.isChecked = task.completed
                text1.text = task.name
                text1.paint.isStrikeThruText = task.completed
                labelPriority.isVisible = task.important
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            ItemTaskBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    interface OnItemClickListener {
        fun onItemClick(task:Task)
        fun onCheckBoxClick(task: Task,isChecked: Boolean)
    }
    
    class DiffCallback: DiffUtil.ItemCallback<Task>(){

        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
    }

}