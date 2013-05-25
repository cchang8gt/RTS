package ai.chris;
import java.util.Comparator;

public class ResourceComparator implements Comparator<Resource> {

	@Override
	public int compare(Resource o1, Resource o2) {
		// TODO Auto-generated method stub
		return o1.distance - o2.distance;
	}

}
