package com.udacity.asteroidradar

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.main.AsteroidAdapter
import com.udacity.asteroidradar.main.MainViewModel

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.contentDescription = "This asteroid is potentially dangerous"

        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
    } else {
        imageView.contentDescription = "This asteroid is considered safe"

        imageView.setImageResource(R.drawable.ic_status_normal)
    }
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {

    imgUrl?.let {

        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Picasso.get()

            .load(imgUri)
            .placeholder(R.drawable.placeholder_picture_of_day)
            .error(R.drawable.placeholder_picture_of_day)
            .into(imgView)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.contentDescription = "This asteroid is potentially dangerous"
        imageView.setImageResource(R.drawable.asteroid_hazardous)
    } else {
        imageView.contentDescription = "This asteroid is considered safe"

        imageView.setImageResource(R.drawable.asteroid_safe)
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
    textView.contentDescription =
        String.format(context.getString(R.string.astronomical_unit_format), number)

}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
    textView.contentDescription = String.format(context.getString(R.string.km_unit_format), number)

}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
    textView.contentDescription =
        String.format(context.getString(R.string.km_s_unit_format), number)

}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Asteroid>?) {
    val adapter = recyclerView.adapter as AsteroidAdapter
    adapter.submitList(data)
}

@BindingAdapter("asteroidApiStatus")
fun bindStatus(statusTextView: TextView, status: MainViewModel.AsteroidApiStatus?) {
    when (status) {
        MainViewModel.AsteroidApiStatus.LOADING -> {
            statusTextView.visibility = View.VISIBLE
            statusTextView.text = "Loading asteroids"
            statusTextView.contentDescription = "Loading asteroids"

        }
        MainViewModel.AsteroidApiStatus.ERROR -> {
            statusTextView.visibility = View.VISIBLE
            statusTextView.text = "Error loading asteroids"
            statusTextView.contentDescription = "Error loading asteroids"

        }
        MainViewModel.AsteroidApiStatus.DONE -> {
            statusTextView.visibility = View.GONE

        }
        MainViewModel.AsteroidApiStatus.NO_NETWORK -> {
            statusTextView.visibility = View.VISIBLE
            statusTextView.text = "No network to load recent asteroids"
            statusTextView.contentDescription = "No network to load recent asteroids"


        }
    }
}


@BindingAdapter("progressStatus")
fun bindProgressStatus(progressBar: ProgressBar, status: MainViewModel.AsteroidApiStatus?) {
    when (status) {
        MainViewModel.AsteroidApiStatus.LOADING -> {
            progressBar.visibility = View.VISIBLE
        }
        MainViewModel.AsteroidApiStatus.ERROR -> {
            progressBar.visibility = View.GONE

        }
        MainViewModel.AsteroidApiStatus.DONE -> {
            progressBar.visibility = View.GONE
        }
        MainViewModel.AsteroidApiStatus.NO_NETWORK -> {
            progressBar.visibility = View.GONE


        }
    }
}