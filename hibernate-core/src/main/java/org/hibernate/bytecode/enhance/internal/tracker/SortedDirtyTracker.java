/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.bytecode.enhance.internal.tracker;

/**
 * small low memory class to keep track of changed fields
 *
 * similar to BasicTracker but where the array is kept ordered to reduce the cost of verifying duplicates
 *
 * @author <a href="mailto:lbarreiro@redhat.com">Luis Barreiro</a>
 */
public final class SortedDirtyTracker {

	private String[] names;

	public SortedDirtyTracker() {
		names = new String[0];
	}

	public void add(String name) {
		// we do a binary search: even if we don't find the name at least we get the position to insert into the array
		int insert = 0;
		for ( int low = 0, high = names.length - 1; low <= high; ) {
			final int middle = low + ( ( high - low ) / 2 );
			if ( names[middle].compareTo( name ) > 0 ) {
				// bottom half: higher bound in (middle - 1) and insert position in middle
				high = middle - 1;
				insert = middle;
			}
			else if( names[middle].compareTo( name ) < 0 ) {
				// top half: lower bound in (middle + 1) and insert position after middle
				insert = low = middle + 1;
			}
			else {
				return;
			}
		}
		final String[] newNames = new String[names.length + 1];
		System.arraycopy( names, 0, newNames, 0, insert);
		System.arraycopy( names, insert, newNames, insert + 1, names.length - insert);
		newNames[insert] = name;
		names = newNames;
	}

	public void clear() {
		names = new String[0];
	}

	public boolean isEmpty() {
		return names.length == 0;
	}

	public String[] get() {
		return names;
	}

}
