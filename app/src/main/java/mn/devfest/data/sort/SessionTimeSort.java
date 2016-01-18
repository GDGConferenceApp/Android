package mn.devfest.data.sort;

import mn.devfest.api.model.Session;
import rx.functions.Func2;

/**
 * Sort function that sorts sessions based on when they occur, from earliest to latest
 *
 * @author bherbst
 */
public class SessionTimeSort implements Func2<Session, Session, Integer> {
    @Override
    public Integer call(Session session, Session session2) {
        return session.getStartTime().compareTo(session2.getStartTime());
    }
}
