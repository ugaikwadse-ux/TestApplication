package com.quicksoft.testapp

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.evrencoskun.tableview.TableView
import com.evrencoskun.tableview.filter.Filter
import com.evrencoskun.tableview.pagination.Pagination
import com.evrencoskun.tableview.pagination.Pagination.OnTableViewPageTurnedListener
import com.quicksoft.testapp.adapter.TableViewAdapter
import com.quicksoft.testapp.adapter.TableViewListener
import com.quicksoft.testapp.adapter.TableViewModel
import com.quicksoft.testapp.helper.ApiManager
import com.quicksoft.testapp.model.ApiResponse
import kotlinx.coroutines.launch

class ApiLoadDataActivity : AppCompatActivity() {
    private lateinit var mTableView: TableView
    private lateinit var cardpreview: CardView
    private lateinit var cardnext: CardView
    private lateinit var tablePaginationDetails: TextView
    private lateinit var mTableFilter: Filter
    private lateinit var mPagination: Pagination
    private lateinit var pageNumberField: EditText
    private var mPaginationEnabled: Boolean = true
    private val currentTextColor: Int = -0x1
    private lateinit var searchField: EditText
    private lateinit var closebutton: AppCompatImageView
    private lateinit var previousButton: AppCompatImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api_load_data)
        closebutton = findViewById(R.id.closebutton)
        cardpreview = findViewById(R.id.cardpreview)
        cardnext = findViewById(R.id.cardnext)
        mTableView = findViewById(R.id.tableview)

        previousButton = findViewById(R.id.previous_button)
        nextButton = findViewById(R.id.next_button)
        pageNumberField = findViewById(R.id.page_number_text)
        tablePaginationDetails = findViewById(R.id.table_details)

        val tableTestContainer = findViewById<View>(R.id.table_test_container)
        val itemsPerPage = findViewById<Spinner>(R.id.items_per_page_spinner)

        if (mPaginationEnabled) {
            tableTestContainer.setVisibility(View.VISIBLE)
            itemsPerPage.setOnItemSelectedListener(onItemsPerPageSelectedListener)

            previousButton.setOnClickListener(mClickListener)
            nextButton.setOnClickListener(mClickListener)
            pageNumberField.addTextChangedListener(onPageTextChanged)
        } else {
            tableTestContainer.setVisibility(View.GONE)
        }

        fetchApiData()

    }

    private fun fetchApiData() {
        ApiManager().fetchApiData(
            this,
            "9876543210",
            "1234",
            "1234",
            "998988200",
            object : ApiManager.FeedApiCallBack {

                override fun onSuccess(response: ApiResponse) {
                    setupTable(response)
                }

                override fun onError(str: String) {
                    // Handle error (Toast / Snackbar / Log)
                }
            }
        )
    }

    private fun setupTable(apiResponse: ApiResponse) {

        val tableViewModel = TableViewModel(this, apiResponse)
        val tableViewAdapter = TableViewAdapter(tableViewModel)

        mTableView.setAdapter(tableViewAdapter)
        mTableView.tableViewListener = TableViewListener(mTableView)

        tableViewAdapter.setAllItems(
            tableViewModel.getColumnHeaderList(),
            tableViewModel.getRowHeaderList(),
            tableViewModel.getCellList()
        )

        if (mPaginationEnabled) {
            mTableFilter = Filter(mTableView)
            mPagination = Pagination(mTableView)
            mPagination.setOnTableViewPageTurnedListener(onTableViewPageTurnedListener)
        }
    }


    private val onItemsPerPageSelectedListener: AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val itemsPerPage = if ("All" == parent.getItemAtPosition(position).toString()) {
                    0
                } else {
                    parent.getItemAtPosition(position).toString().toInt()
                }
                setTableItemsPerPage(itemsPerPage)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

    fun filterTable(filter: String) {
        mTableFilter.set(filter)
    }


    fun nextTablePage() {
        mPagination.nextPage()
    }

    fun previousTablePage() {
        mPagination.previousPage()
    }

    fun goToTablePage(page: Int) {
        mPagination.goToPage(page)
    }

    fun setTableItemsPerPage(itemsPerPage: Int) {
        if (::mPagination.isInitialized) {
            mPagination.itemsPerPage = itemsPerPage
        }
    }


    private val onTableViewPageTurnedListener =
        OnTableViewPageTurnedListener { numItems, itemsStart, itemsEnd ->
            val currentPage = mPagination.currentPage
            val pageCount = mPagination.pageCount
            previousButton.visibility = View.VISIBLE
            cardpreview.visibility = View.VISIBLE
            nextButton.visibility = View.VISIBLE
            cardnext.visibility = View.VISIBLE

            if (currentPage == 1 && pageCount == 1) {
                previousButton.visibility = View.INVISIBLE
                cardpreview.visibility = View.INVISIBLE
                nextButton.visibility = View.INVISIBLE
                cardnext.visibility = View.INVISIBLE
            }

            if (currentPage == 1) {
                previousButton.visibility = View.INVISIBLE
                cardpreview.visibility = View.INVISIBLE
            }

            if (currentPage == pageCount) {
                nextButton.visibility = View.INVISIBLE
                cardnext.visibility = View.INVISIBLE
            }
            tablePaginationDetails.text = getString(
                R.string.table_pagination_details,
                currentPage.toString(),
                itemsStart.toString(),
                itemsEnd.toString()
            )
        }

    private val mItemSelectionListener: AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                // 0. index is for empty item of spinner.
                if (position > 0) {
                    val filter = position.toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Left empty intentionally.
            }
        }

    private val mSearchTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val input = searchField.text.toString()
            if (input.length > 0) {
                closebutton.visibility = View.VISIBLE
                closebutton.setOnClickListener { view13: View? -> searchField.setText("") }
            } else {
                closebutton.visibility = View.GONE
            }
            filterTable(s.toString())
        }

        override fun afterTextChanged(s: Editable) {
        }
    }


    private val mClickListener =
        View.OnClickListener { v ->
            if (v === previousButton) {
                previousTablePage()
            } else if (v === nextButton) {
                nextTablePage()
            }
        }

    private val onPageTextChanged: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val page = if (TextUtils.isEmpty(s)) {
                1
            } else {
                s.toString().toInt()
            }

            goToTablePage(page)
        }

        override fun afterTextChanged(s: Editable) {
        }
    }

}