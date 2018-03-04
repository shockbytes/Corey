package at.shockbytes.corey.fragment


import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import at.shockbytes.corey.R
import butterknife.ButterKnife
import butterknife.Unbinder

class RunningFragment : Fragment() {

    private var unbinder: Unbinder? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle): View? {
        val v = inflater.inflate(R.layout.fragment_running, container, false)
        unbinder = ButterKnife.bind(this, v)
        return v
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder?.unbind()
    }

    companion object {

        fun newInstance(): RunningFragment {
            val fragment = RunningFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
