package wottrich.github.io.quicklychecklist.impl.presentation.viewmodels

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import wottrich.github.io.datasource.entity.NewTask
import wottrich.github.io.tools.base.BaseViewModel

class QuicklyChecklistViewModel(
    quicklyChecklistJson: String
) : BaseViewModel() {

    init {
        val tasks = Gson().fromJson<List<NewTask>>(
            quicklyChecklistJson,
            object : TypeToken<List<NewTask>>() {}.type
        )
        println(tasks)
    }

}