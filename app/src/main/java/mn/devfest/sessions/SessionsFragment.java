package mn.devfest.sessions;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import mn.devfest.R;
import mn.devfest.api.DevFestDataSource;
import mn.devfest.api.model.Session;
import mn.devfest.view.decoration.DividerItemDecoration;

/**
 * Fragment that displays the available sessions
 *
 * @author bherbst
 * @author pfuentes
 */
public class SessionsFragment extends Fragment {

    @Bind(R.id.session_list_recyclerview)
    RecyclerView mSessionRecyclerView;

    private SessionListAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private ArrayList<Session> sessionData = new ArrayList<>();
    private DevFestDataSource.DataSourceListener dataSource; // TODO: There is probably a 'Dagger' way to inject the data source

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sessions, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof DevFestDataSource.DataSourceListener) {
            dataSource = (DevFestDataSource.DataSourceListener)context;
            sessionData = dataSource.onSessionsUpdate();
        }
    }

    /**
     * Clear callback on detach to prevent null reference errors after the view has been
     */
    @Override
    public void onDetach() {
        super.onDetach();
        dataSource = null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        //TODO set data real date on adapter & remove dummy data

        mAdapter = new SessionListAdapter();
        mAdapter.setSessions(sessionData);
        mSessionRecyclerView.setAdapter(mAdapter);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mSessionRecyclerView.setLayoutManager(mLinearLayoutManager);
        mSessionRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

    }
}
