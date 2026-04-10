package edu.cnm.deepdive.seesomethingabq.controller

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.snackbar.Snackbar
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.AndroidEntryPoint
import edu.cnm.deepdive.seesomethingabq.BuildConfig
import edu.cnm.deepdive.seesomethingabq.R
import edu.cnm.deepdive.seesomethingabq.databinding.FragmentReportDetailBinding
import edu.cnm.deepdive.seesomethingabq.model.domain.PickedLocation
import edu.cnm.deepdive.seesomethingabq.model.domain.PlacePredictionCandidate
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReport
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType
import edu.cnm.deepdive.seesomethingabq.viewmodel.IssueReportViewModel
import edu.cnm.deepdive.seesomethingabq.viewmodel.IssueTypeViewModel
import edu.cnm.deepdive.seesomethingabq.viewmodel.ReportDetailImageEditViewModel
import java.io.File
import java.io.IOException
import java.util.Locale
import java.util.Objects
import com.google.android.material.chip.ChipGroup

@AndroidEntryPoint
class ReportDetailFragment : Fragment() {

    companion object {
        private const val TAG = "ReportDetailFragment"
        private const val SEARCH_DEBOUNCE_MS = 500L
        private const val MIN_QUERY_LENGTH = 3
    }

    private var _binding: FragmentReportDetailBinding? = null
    private val binding: FragmentReportDetailBinding
        get() = _binding!!

    private val viewModel: IssueReportViewModel by viewModels()
    private val issueTypeViewModel: IssueTypeViewModel by viewModels()
    private val imageEditViewModel: ReportDetailImageEditViewModel by viewModels()
    private val args: ReportDetailFragmentArgs by navArgs()

    private var loadedReport: IssueReport? = null
    private var originalReport: IssueReport? = null
    private var editing: Boolean = false
    private val selectedIssueTypeTags: MutableSet<String> = linkedSetOf()
    private var availableIssueTypes: List<IssueType> = emptyList()

    private var confirmedLocation: PickedLocation? = null
    private var applyingPickedLocation: Boolean = false

    private lateinit var locationCandidateAdapter: LocationCandidateAdapter
    private val debounceHandler = Handler(Looper.getMainLooper())
    private var pendingSearch: Runnable? = null
    private var currentLocationCancellationTokenSource: CancellationTokenSource? = null

    private lateinit var placesClient: PlacesClient
    private lateinit var sessionToken: AutocompleteSessionToken
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var takePhotoLauncher: ActivityResultLauncher<Uri>
    private lateinit var pickGalleryImageLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    private var pendingCaptureUri: Uri? = null
    private var pendingCaptureFile: File? = null

    private var editableImagesAdapter: ReportDetailImageEditAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportDetailBinding.inflate(inflater, container, false)

        binding.editButton.setOnClickListener {
            setEditing(true)
        }

        binding.saveButton.setOnClickListener {
            save()
        }

        binding.cancelButton.setOnClickListener {
            cancelEdits()
        }

        initializeImagePickers()
        setupImagesSection()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializePlaces()
        initializeLocationServices()
        setupLocationVisibilityAssist()
        setupLocationResultsList()
        setupInlineLocationSearch()

        binding.locationLayout.setEndIconOnClickListener {
            if (!editing) {
                return@setEndIconOnClickListener
            }
            binding.locationInput.setText(null)
            confirmedLocation = null
            binding.locationLayout.error = null
            binding.locationLayout.helperText = null
            hideLocationResults()
        }

        binding.useCurrentLocationButton.setOnClickListener {
            if (editing) {
                requestCurrentLocation()
            }
        }

        showLocationPlaceholder(getString(R.string.location_search_placeholder))
        setEditing(false)

        issueTypeViewModel.issueTypes.observe(viewLifecycleOwner) { issueTypes ->
            if (issueTypes != null) {
                availableIssueTypes = issueTypes
                populateIssueTypeChips()
            }
        }
        issueTypeViewModel.refresh(requireActivity())

        imageEditViewModel.state.observe(viewLifecycleOwner) { state ->
            if (!editing) {
                return@observe
            }
            val adapter = editableImagesAdapter ?: return@observe
            adapter.submitList(state.visibleItems)
            renderImageEmptyState(state.visibleItems.isEmpty())
        }
    }

    override fun onResume() {
        super.onResume()

        // Returning from camera/gallery would otherwise reload and blow away the edit-session UI state.
        if (editing) {
            return
        }

        val reportId = args.reportId
        viewModel.getReport(requireActivity(), reportId)
            .thenAccept { report ->
                requireActivity().runOnUiThread {
                    val binding = _binding ?: return@runOnUiThread
                    loadedReport = report
                    originalReport = report
                    selectedIssueTypeTags.clear()
                    selectedIssueTypeTags.addAll(report.issueTypes)

                    binding.descriptionInput.setText(report.description.orEmpty())
                    binding.acceptedStateValue.text = report.acceptedState ?: "Unknown"
                    binding.locationInput.setText(bestLocationText(report))
                    seedConfirmedLocation(report)
                    populateIssueTypeChips()
                    setEditing(false)

                    val images = (report.reportImages ?: emptyList())
                        .sortedBy { it.albumOrder }

                    imageEditViewModel.seedFromReport(images)

                    val adapter = ReportImageThumbnailAdapter(
                        requireActivity(),
                        report.externalId,
                        images
                    ) { reportId, imageId, mimeType ->
                        viewModel.downloadImageToCache(requireActivity(), reportId, imageId, mimeType)
                    }

                    if (images.isEmpty()) {
                        binding.imageList.visibility = View.GONE
                        binding.noImagesPlaceholder.visibility = View.VISIBLE
                    } else {
                        binding.noImagesPlaceholder.visibility = View.GONE
                        binding.imageList.visibility = View.VISIBLE
                        binding.imageList.layoutManager = GridLayoutManager(requireContext(), 3)
                        binding.imageList.adapter = adapter
                    }
                }
            }
    }

    private fun setupImagesSection() {
        binding.takePhotoButton.setOnClickListener {
            if (editing) {
                launchCamera()
            }
        }
        binding.attachGalleryImageButton.setOnClickListener {
            if (editing) {
                pickGalleryImageLauncher.launch(
                    PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        .build()
                )
            }
        }
        binding.imageList.layoutManager = GridLayoutManager(requireContext(), 3)
    }

    private fun initializeImagePickers() {
        pickGalleryImageLauncher = registerForActivityResult(
            ActivityResultContracts.PickMultipleVisualMedia(5)
        ) { uris ->
            if (uris != null && uris.isNotEmpty()) {
                imageEditViewModel.stageAddLocalUris(uris)
            }
        }

        takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success == true) {
                pendingCaptureUri?.let { imageEditViewModel.stageAddLocalUris(listOf(it)) }
            } else {
                pendingCaptureUri?.let { safeDeleteIfOwnedAttachment(it) }
            }
            pendingCaptureUri = null
            pendingCaptureFile = null
        }
    }

    private fun launchCamera() {
        try {
            pendingCaptureFile = createTempCameraFile()
            pendingCaptureUri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().packageName + ".fileprovider",
                pendingCaptureFile!!
            )
            pendingCaptureUri?.let { takePhotoLauncher.launch(it) }
        } catch (e: IOException) {
            Log.e(TAG, "Unable to create temp camera file", e)
            pendingCaptureUri = null
            pendingCaptureFile = null
            Snackbar.make(binding.root, R.string.take_photo_failure, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun createTempCameraFile(): File {
        val cacheDir = requireContext().cacheDir
        val cameraDir = File(cacheDir, "camera")
        cameraDir.mkdirs()
        return File.createTempFile("issue_report_", ".jpg", cameraDir)
    }

    private fun renderImageEmptyState(isEmpty: Boolean) {
        val binding = _binding ?: return
        if (isEmpty) {
            binding.imageList.visibility = View.GONE
            binding.noImagesPlaceholder.visibility = View.VISIBLE
        } else {
            binding.noImagesPlaceholder.visibility = View.GONE
            binding.imageList.visibility = View.VISIBLE
        }
    }

    private fun initializePlaces() {
        val apiKey = BuildConfig.PLACES_API_KEY
        if (apiKey.isBlank() || apiKey == "DEFAULT_API_KEY") {
            throw IllegalStateException("Places API key is missing.")
        }
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(
                requireContext().applicationContext,
                apiKey
            )
        }
        placesClient = Places.createClient(requireContext())
        sessionToken = AutocompleteSessionToken.newInstance()
    }

    private fun initializeLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationPermissionLauncher =
            registerForActivityResult(RequestPermission()) { granted ->
                if (granted == true) {
                    fetchCurrentLocation()
                } else if (_binding != null) {
                    Snackbar.make(
                        binding.root,
                        R.string.location_permission_denied,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun setupLocationVisibilityAssist() {
        binding.locationInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && editing) {
                scrollLocationSectionIntoView()
            }
        }
    }

    private fun scrollLocationSectionIntoView() {
        val binding = _binding ?: return
        binding.root.post {
            val extraTopSpace = resources.getDimensionPixelSize(R.dimen.full_dynamic_spacing)
            val targetY = maxOf(0, binding.locationLayout.top - extraTopSpace)
            binding.root.smoothScrollTo(0, targetY)
        }
    }

    private fun setupLocationResultsList() {
        locationCandidateAdapter = LocationCandidateAdapter { candidate ->
            fetchSelectedPlace(candidate)
        }
        binding.locationResultsList.layoutManager = LinearLayoutManager(requireContext())
        binding.locationResultsList.adapter = locationCandidateAdapter
    }

    private fun setupInlineLocationSearch() {
        binding.locationInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!editing) {
                    return
                }

                if (!applyingPickedLocation) {
                    invalidateConfirmedLocation()
                }

                pendingSearch?.let {
                    debounceHandler.removeCallbacks(it)
                    pendingSearch = null
                }

                val query = s?.toString()?.trim().orEmpty()

                if (applyingPickedLocation) {
                    hideLocationResults()
                    return
                }

                if (query.length >= MIN_QUERY_LENGTH) {
                    pendingSearch = Runnable { performLocationSearch(query) }
                    debounceHandler.postDelayed(pendingSearch!!, SEARCH_DEBOUNCE_MS)
                } else {
                    locationCandidateAdapter.setCandidates(emptyList())
                    showLocationPlaceholder(getString(R.string.location_search_placeholder))
                }
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun performLocationSearch(query: String) {
        showLocationLoading()
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(sessionToken)
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response -> handleLocationPredictions(response) }
            .addOnFailureListener { error ->
                Log.e(TAG, "Autocomplete failed.", error)
                if (_binding != null) {
                    showLocationPlaceholder(getString(R.string.location_search_failed))
                }
            }
    }

    private fun handleLocationPredictions(response: FindAutocompletePredictionsResponse) {
        if (_binding == null || !editing) {
            return
        }

        val candidates = response.autocompletePredictions
            .map { prediction -> toCandidate(prediction) }

        if (candidates.isEmpty()) {
            showLocationPlaceholder(getString(R.string.location_no_results))
        } else {
            showLocationCandidates(candidates)
        }
    }

    private fun toCandidate(prediction: AutocompletePrediction): PlacePredictionCandidate {
        return PlacePredictionCandidate(
            prediction.placeId,
            prediction.getFullText(null).toString()
        )
    }

    private fun fetchSelectedPlace(candidate: PlacePredictionCandidate) {
        showLocationLoading()

        val fields = listOf(
            Place.Field.ID,
            Place.Field.DISPLAY_NAME,
            Place.Field.FORMATTED_ADDRESS,
            Place.Field.LOCATION
        )

        val request = FetchPlaceRequest.builder(candidate.placeId, fields)
            .setSessionToken(sessionToken)
            .build()

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response -> handleFetchedPlace(response) }
            .addOnFailureListener { error ->
                Log.e(TAG, "Fetch place failed.", error)
                if (_binding != null) {
                    showLocationPlaceholder(getString(R.string.location_search_failed))
                }
            }
    }

    private fun handleFetchedPlace(response: FetchPlaceResponse) {
        if (_binding == null || !editing) {
            return
        }

        val place = response.place
        val latLng = place.location
        if (latLng == null) {
            showLocationPlaceholder(getString(R.string.location_search_failed))
            return
        }

        var displayText = place.formattedAddress
        if (displayText.isNullOrBlank()) {
            displayText = place.displayName?.toString()
        }
        if (displayText.isNullOrBlank()) {
            showLocationPlaceholder(getString(R.string.location_search_failed))
            return
        }

        val location = PickedLocation(displayText, latLng.latitude, latLng.longitude)
        applyConfirmedLocation(location)
        sessionToken = AutocompleteSessionToken.newInstance()
        hideLocationResults()
    }

    private fun requestCurrentLocation() {
        val fineGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            fetchCurrentLocation()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun fetchCurrentLocation() {
        if (_binding == null) {
            return
        }

        val fineGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            Snackbar.make(
                binding.root,
                R.string.location_permission_denied,
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }

        showLocationLoading()
        binding.locationResultsPlaceholder.setText(R.string.location_fetching_current)

        val request = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMaxUpdateAgeMillis(10_000)
            .build()

        currentLocationCancellationTokenSource = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            request,
            currentLocationCancellationTokenSource!!.token
        )
            .addOnSuccessListener { location ->
                if (_binding == null) {
                    return@addOnSuccessListener
                }
                if (location == null) {
                    showLocationPlaceholder(getString(R.string.location_unavailable))
                    return@addOnSuccessListener
                }
                reverseGeocodeCurrentLocation(location)
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "Current location lookup failed.", error)
                if (_binding != null) {
                    showLocationPlaceholder(getString(R.string.location_unavailable))
                }
            }
    }

    private fun reverseGeocodeCurrentLocation(location: Location) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1,
                object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        requireActivity().runOnUiThread {
                            if (_binding != null) {
                                handleReverseGeocodeResult(location, addresses)
                            }
                        }
                    }

                    override fun onError(errorMessage: String?) {
                        Log.e(TAG, "Reverse geocoding failed: $errorMessage")
                        requireActivity().runOnUiThread {
                            if (_binding != null) {
                                showLocationPlaceholder(getString(R.string.location_unavailable))
                            }
                        }
                    }
                }
            )
        } else {
            Thread {
                var addresses: List<Address> = emptyList()
                try {
                    val result = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (result != null) {
                        addresses = result
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "Reverse geocoding failed.", e)
                } catch (e: RuntimeException) {
                    Log.e(TAG, "Reverse geocoding failed.", e)
                }

                requireActivity().runOnUiThread {
                    if (_binding != null) {
                        handleReverseGeocodeResult(location, addresses)
                    }
                }
            }.start()
        }
    }

    private fun handleReverseGeocodeResult(location: Location, addresses: List<Address>) {
        if (_binding == null || !editing) {
            return
        }

        var displayText: String? = null
        if (addresses.isNotEmpty()) {
            val address = addresses.first()
            displayText = address.getAddressLine(0)
            if (displayText.isNullOrBlank()) {
                val parts = mutableListOf<String>()
                address.featureName?.let { parts.add(it) }
                address.thoroughfare?.let { parts.add(it) }
                address.locality?.let { parts.add(it) }
                displayText = parts.joinToString(", ")
            }
        }

        if (displayText.isNullOrBlank()) {
            showLocationPlaceholder(getString(R.string.location_unavailable))
            return
        }

        val pickedLocation = PickedLocation(
            displayText,
            location.latitude,
            location.longitude
        )
        applyConfirmedLocation(pickedLocation)
        hideLocationResults()
    }

    private fun resetLocationPickerTransientState() {
        pendingSearch?.let {
            debounceHandler.removeCallbacks(it)
            pendingSearch = null
        }

        currentLocationCancellationTokenSource?.cancel()
        currentLocationCancellationTokenSource = null

        if (this::locationCandidateAdapter.isInitialized) {
            locationCandidateAdapter.setCandidates(emptyList())
        }

        val binding = _binding ?: return
        binding.locationLoadingIndicator.visibility = View.GONE
        binding.locationResultsList.visibility = View.GONE
        binding.locationResultsPlaceholder.visibility = View.GONE
    }

    private fun setEditing(editing: Boolean) {
        this.editing = editing

        binding.descriptionInput.isEnabled = editing
        binding.descriptionInput.isFocusable = editing
        binding.descriptionInput.isFocusableInTouchMode = editing

        binding.locationInput.isEnabled = editing
        binding.locationInput.isFocusable = editing
        binding.locationInput.isFocusableInTouchMode = editing
        binding.useCurrentLocationButton.isEnabled = editing

        binding.takePhotoButton.isEnabled = editing
        binding.attachGalleryImageButton.isEnabled = editing

        populateIssueTypeChips()

        if (!editing) {
            hideLocationResults()
            binding.locationLayout.error = null
            binding.locationInput.clearFocus()
        }

        binding.saveButton.visibility = if (editing) View.VISIBLE else View.GONE
        binding.cancelButton.visibility = if (editing) View.VISIBLE else View.GONE
        binding.editButton.visibility = if (editing) View.GONE else View.VISIBLE

        val report = loadedReport ?: originalReport
        if (editing && report != null) {
            ensureEditableImagesAdapter(report.externalId)
            editableImagesAdapter?.editing = true
            binding.imageList.adapter = editableImagesAdapter
            val state = imageEditViewModel.state.value
            if (state != null) {
                editableImagesAdapter?.submitList(state.visibleItems)
                renderImageEmptyState(state.visibleItems.isEmpty())
            }
        } else if (report != null) {
            editableImagesAdapter?.editing = false
            val images = (report.reportImages ?: emptyList()).sortedBy { it.albumOrder }
            val adapter = ReportImageThumbnailAdapter(
                requireActivity(),
                report.externalId,
                images
            ) { reportId, imageId, mimeType ->
                viewModel.downloadImageToCache(requireActivity(), reportId, imageId, mimeType)
            }
            binding.imageList.adapter = adapter
            renderImageEmptyState(images.isEmpty())
        }
    }

    private fun ensureEditableImagesAdapter(reportId: String) {
        if (editableImagesAdapter != null) {
            return
        }
        editableImagesAdapter = ReportDetailImageEditAdapter(
            requireActivity(),
            reportId,
            { rId, imageId, mimeType ->
                viewModel.downloadImageToCache(requireActivity(), rId, imageId, mimeType)
            },
            onRemoveServerImage = { imageId ->
                imageEditViewModel.stageRemoveServerImage(imageId)
            },
            onRemoveLocalImage = { uri ->
                imageEditViewModel.stageRemoveLocalUri(uri)
                safeDeleteIfOwnedAttachment(uri)
            }
        ).apply { editing = true }
    }

    private fun cancelEdits() {
        val original = originalReport ?: return
        val binding = _binding ?: return

        resetLocationPickerTransientState()

        binding.descriptionInput.setText(original.description ?: "")
        binding.locationInput.setText(bestLocationText(original))

        selectedIssueTypeTags.clear()
        selectedIssueTypeTags.addAll(original.issueTypes)

        seedConfirmedLocation(original)
        binding.locationLayout.error = null
        binding.locationLayout.helperText = null
        hideLocationResults()

        cleanupStagedLocalImages()
        imageEditViewModel.resetToOriginal()

        populateIssueTypeChips()
        setEditing(false)
    }

    private fun cleanupStagedLocalImages() {
        val state = imageEditViewModel.state.value ?: return
        state.localUrisStagedForUpload.forEach { safeDeleteIfOwnedAttachment(it) }
    }

    private fun safeDeleteIfOwnedAttachment(uri: Uri) {
        val authority = requireContext().packageName + ".fileprovider"
        if (uri.authority != authority) {
            return
        }
        try {
            requireContext().contentResolver.delete(uri, null, null)
        } catch (e: RuntimeException) {
            Log.w(TAG, "Unable to delete temp attachment $uri", e)
        }
    }

    private fun save() {
        val current = loadedReport ?: return

        val description = binding.descriptionInput.text?.toString()?.trim().orEmpty()
        val issueTypes = selectedIssueTypeTags.toList()

        val locationText = binding.locationInput.text?.toString()?.trim().orEmpty()
        val pickedLocation = confirmedLocation

        binding.locationLayout.error = null

        if (pickedLocation == null) {
            binding.locationLayout.helperText = null
            binding.locationLayout.error = if (locationText.isEmpty()) {
                getString(R.string.location_required)
            } else {
                getString(R.string.location_not_confirmed)
            }
            return
        }

        val request = IssueReportRequest(
            textDescription = description,
            latitude = pickedLocation.latitude,
            longitude = pickedLocation.longitude,
            streetCoordinate = pickedLocation.displayText,
            locationDescription = null,
            issueTypes = issueTypes
        )

        viewModel.updateReport(requireActivity(), current.externalId, request)
            .thenAccept { saved ->
                requireActivity().runOnUiThread {
                    val binding = _binding ?: return@runOnUiThread
                    loadedReport = saved
                    originalReport = saved

                    binding.descriptionInput.setText(saved.description.orEmpty())
                    binding.acceptedStateValue.text = saved.acceptedState ?: "Unknown"
                    binding.locationInput.setText(bestLocationText(saved))
                    seedConfirmedLocation(saved)

                    selectedIssueTypeTags.clear()
                    selectedIssueTypeTags.addAll(saved.issueTypes)
                    populateIssueTypeChips()
                    setEditing(false)

                    Snackbar.make(binding.root, "Saved", Snackbar.LENGTH_SHORT).show()
                    findNavController().previousBackStackEntry?.savedStateHandle
                        ?.set(UserDashboardRefresh.USER_REPORTS_REFRESH_REQUIRED, true)
                    findNavController().popBackStack()
                }
            }
            .exceptionally { thrown ->
                requireActivity().runOnUiThread {
                    val binding = _binding ?: return@runOnUiThread
                    Snackbar.make(
                        binding.root,
                        thrown?.message ?: "Save failed",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                null
            }
    }

    private fun seedConfirmedLocation(report: IssueReport) {
        val displayText = bestLocationTextOrNull(report)
        confirmedLocation = if (!displayText.isNullOrBlank()) {
            PickedLocation(displayText, report.latitude, report.longitude)
        } else {
            null
        }
    }

    private fun applyConfirmedLocation(location: PickedLocation) {
        confirmedLocation = location
        applyingPickedLocation = true
        binding.locationInput.setText(location.displayText)
        binding.locationLayout.error = null
        binding.locationLayout.helperText = getString(R.string.location_confirmed)
        applyingPickedLocation = false
        binding.locationInput.clearFocus()
        hideKeyboard()
        hideLocationResults()
    }

    private fun invalidateConfirmedLocation() {
        val hadConfirmed = confirmedLocation != null
        confirmedLocation = null
        if (_binding != null && hadConfirmed) {
            binding.locationLayout.error = null
            binding.locationLayout.helperText = getString(R.string.location_unconfirmed_edit)
        }
    }

    private fun bestLocationText(report: IssueReport): String {
        return bestLocationTextOrNull(report) ?: "${report.latitude}, ${report.longitude}"
    }

    private fun bestLocationTextOrNull(report: IssueReport): String? {
        val locationDescription = report.locationDescription?.trim()
        if (!locationDescription.isNullOrEmpty()) {
            return locationDescription
        }
        val streetCoordinate = report.streetCoordinate?.trim()
        if (!streetCoordinate.isNullOrEmpty()) {
            return streetCoordinate
        }
        return null
    }

    private fun populateIssueTypeChips() {
        val binding = _binding ?: return
        binding.issueTypeChipGroup.removeAllViews()
        for (issueType in availableIssueTypes) {
            val tag = issueType.issueTypeTag
            val chip = Chip(requireContext())
            chip.setChipDrawable(
                ChipDrawable.createFromAttributes(
                    requireContext(),
                    null,
                    0,
                    com.google.android.material.R.style.Widget_Material3_Chip_Filter
                )
            )
            chip.text = tag
            chip.isCheckable = true
            chip.isChecked = selectedIssueTypeTags.contains(tag)
            chip.isEnabled = editing
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (!editing) {
                    return@setOnCheckedChangeListener
                }
                if (isChecked) {
                    selectedIssueTypeTags.add(tag)
                } else {
                    selectedIssueTypeTags.remove(tag)
                }
            }
            binding.issueTypeChipGroup.addView(chip)
        }
    }

    private fun showLocationLoading() {
        val binding = _binding ?: return
        binding.locationLoadingIndicator.visibility = View.VISIBLE
        binding.locationResultsList.visibility = View.GONE
        binding.locationResultsPlaceholder.visibility = View.GONE
    }

    private fun showLocationCandidates(candidates: List<PlacePredictionCandidate>) {
        val binding = _binding ?: return
        binding.locationLoadingIndicator.visibility = View.GONE
        binding.locationResultsPlaceholder.visibility = View.GONE
        binding.locationResultsList.visibility = View.VISIBLE
        locationCandidateAdapter.setCandidates(candidates)
        scrollLocationSectionIntoView()
    }

    private fun showLocationPlaceholder(message: String) {
        val binding = _binding ?: return
        binding.locationLoadingIndicator.visibility = View.GONE
        binding.locationResultsList.visibility = View.GONE
        binding.locationResultsPlaceholder.visibility = View.VISIBLE
        binding.locationResultsPlaceholder.text = message
    }

    private fun hideLocationResults() {
        val binding = _binding ?: return
        binding.locationLoadingIndicator.visibility = View.GONE
        binding.locationResultsList.visibility = View.GONE
        binding.locationResultsPlaceholder.visibility = View.GONE
        locationCandidateAdapter.setCandidates(emptyList())
    }

    private fun hideKeyboard() {
        val binding = _binding ?: return
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding.locationInput.windowToken, 0)
    }

    override fun onDestroyView() {
        pendingSearch?.let {
            debounceHandler.removeCallbacks(it)
            pendingSearch = null
        }
        currentLocationCancellationTokenSource?.cancel()
        currentLocationCancellationTokenSource = null
        if (activity?.isChangingConfigurations != true) {
            cleanupStagedLocalImages()
        }
        _binding = null
        super.onDestroyView()
    }
}
