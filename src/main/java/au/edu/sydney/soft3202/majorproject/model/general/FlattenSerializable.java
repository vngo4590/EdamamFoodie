package au.edu.sydney.soft3202.majorproject.model.general;

import java.io.Serializable;

/** An interface that allows a class to flatten down to report string */
public interface FlattenSerializable extends Serializable {
  String flatten();
}
