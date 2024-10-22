package project.android.demo.autofillframework

import android.app.assist.AssistStructure
import android.app.assist.AssistStructure.ViewNode
import android.os.CancellationSignal
import android.service.autofill.*
import android.support.v4.util.ArrayMap
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import android.widget.Toast

class MyAutofillService : AutofillService() {

    override fun onFillRequest(
        request: FillRequest, cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {

        val structure = request.fillContexts[request.fillContexts.size - 1].structure
        val fields = parseAutofillableFields(structure)

        val response = FillResponse.Builder()

        for(i in 1..3) {
            val set = Dataset.Builder()

            for((hint, id) in fields) {

                val value = i.toString() + " " + hint
                val view = createRemoteViews(value)
                set.setValue(id, AutofillValue.forText(value), view)
            }

            response.addDataset(set.build())
        }

        callback.onSuccess(response.build())
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        callback.onSuccess()
    }

    /**
     * Parse the AssistStructure that represents the Activity that is being autofilled.
     */
    private fun parseAutofillableFields(structure: AssistStructure): Map<String, AutofillId> {
        val autoFillFields = ArrayMap<String, AutofillId>()
        for (i in 0 until structure.windowNodeCount) {
            val node = structure.getWindowNodeAt(i).rootViewNode
            addAutofillableFields(autoFillFields, node)
        }
        return autoFillFields
    }

    /**
     * Adds any autofillable view from the [ViewNode] and its descendants to the map.
     */
    private fun addAutofillableFields(
        autoFillFields: MutableMap<String, AutofillId>,
        node: ViewNode
    ) {
        val hints = node.autofillHints
        val id = node.autofillId // id = unique identifier of an autofill node
        if (hints != null && id != null) {
            val hint = hints[0].toLowerCase()

            if (!autoFillFields.containsKey(hint)) {
                autoFillFields[hint] = id
            }
        }

        for (i in 0 until node.childCount) {
            // repeat the process for all children (if there are any)
            addAutofillableFields(autoFillFields, node.getChildAt(i))
        }
    }

    private fun createRemoteViews(
        text: CharSequence
    ): RemoteViews {
        val presentation = RemoteViews(packageName, R.layout.autofill_list_item)
        presentation.setTextViewText(R.id.text, text)
        //presentation.setImageViewResource(R.id.image, R.mipmap.ic_launcher)
        return presentation
    }
}