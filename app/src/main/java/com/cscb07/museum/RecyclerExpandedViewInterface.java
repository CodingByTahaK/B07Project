package com.cscb07.museum;

//needed to add this as a sepertate interface class so I can implment it in RecyclerViewFragment,
//then I need to pass an instance of it to artifactAdapter in RecylcerViewFragment
public interface RecyclerExpandedViewInterface {
    void onArtifactClick(int position);
}
