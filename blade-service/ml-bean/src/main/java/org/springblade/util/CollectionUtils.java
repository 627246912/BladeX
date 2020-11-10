package org.springblade.util;

import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * Miscellaneous collection utility methods. Mainly for internal use within the framework.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 1.1.3
 */
public abstract class CollectionUtils {

	/**
	 * Return <code>true</code> if the supplied Collection is <code>null</code> or empty. Otherwise, return
	 * <code>false</code>.
	 *
	 * @param collection
	 *            the Collection to check
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Collection collection) {
		return collection == null || collection.isEmpty();
	}

	/**
	 * Return <code>true</code> if the supplied Map is <code>null</code> or empty. Otherwise, return <code>false</code>.
	 *
	 * @param map
	 *            the Map to check
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Map map) {
		return map == null || map.isEmpty();
	}

	/**
	 * Check whether the given Collection contains the given element instance.
	 * <p>
	 * Enforces the given instance to be present, rather than returning <code>true</code> for an equal element as well.
	 *
	 * @param collection
	 *            the Collection to check
	 * @param element
	 *            the element to look for
	 * @return <code>true</code> if found, <code>false</code> else
	 */
	@SuppressWarnings("rawtypes")
	public static boolean containsInstance(Collection collection, Object element) {
		if (collection != null) {
			for (Iterator it = collection.iterator(); it.hasNext();) {
				Object candidate = it.next();
				if (candidate.equals(element)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check whether the given Iterator contains the given element.
	 *
	 * @param iterator
	 *            the Iterator to check
	 * @param element
	 *            the element to look for
	 * @return <code>true</code> if found, <code>false</code> else
	 */
	@SuppressWarnings("rawtypes")
	public static boolean contains(Iterator iterator, Object element) {
		if (iterator != null) {
			while (iterator.hasNext()) {
				Object candidate = iterator.next();
				if (ObjectUtils.nullSafeEquals(candidate, element)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check whether the given Enumeration contains the given element.
	 *
	 * @param enumeration
	 *            the Enumeration to check
	 * @param element
	 *            the element to look for
	 * @return <code>true</code> if found, <code>false</code> else
	 */
	@SuppressWarnings("rawtypes")
	public static boolean contains(Enumeration enumeration, Object element) {
		if (enumeration != null) {
			while (enumeration.hasMoreElements()) {
				Object candidate = enumeration.nextElement();
				if (ObjectUtils.nullSafeEquals(candidate, element)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Determine whether the given Collection only contains a single unique object.
	 *
	 * @param collection
	 *            the Collection to check
	 * @return <code>true</code> if the collection contains a single reference or multiple references to the same
	 *         instance, <code>false</code> else
	 */
	@SuppressWarnings("rawtypes")
	public static boolean hasUniqueObject(Collection collection) {
		if (isEmpty(collection)) {
			return false;
		}
		boolean hasCandidate = false;
		Object candidate = null;
		for (Iterator it = collection.iterator(); it.hasNext();) {
			Object elem = it.next();
			if (!hasCandidate) {
				hasCandidate = true;
				candidate = elem;
			} else if (!candidate.equals(elem)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Find a value of the given type in the given Collection.
	 *
	 * @param collection
	 *            the Collection to search
	 * @param type
	 *            the type to look for
	 * @return a value of the given type found, or <code>null</code> if none
	 * @throws IllegalArgumentException
	 *             if more than one value of the given type found
	 */
	@SuppressWarnings("rawtypes")
	public static Object findValueOfType(Collection collection, Class type) throws IllegalArgumentException {
		if (isEmpty(collection)) {
			return null;
		}
		Class typeToUse = type != null ? type : Object.class;
		Object value = null;
		for (Iterator it = collection.iterator(); it.hasNext();) {
			Object obj = it.next();
			if (typeToUse.isInstance(obj)) {
				if (value != null) {
					throw new IllegalArgumentException("More than one value of type [" + typeToUse.getName()
							+ "] found");
				}
				value = obj;
			}
		}
		return value;
	}

	/**
	 * Find a value of one of the given types in the given Collection: searching the Collection for a value of the first
	 * type, then searching for a value of the second type, etc.
	 *
	 * @param collection
	 *            the collection to search
	 * @param types
	 *            the types to look for, in prioritized order
	 * @return a of one of the given types found, or <code>null</code> if none
	 * @throws IllegalArgumentException
	 *             if more than one value of the given type found
	 */
	@SuppressWarnings("rawtypes")
	public static Object findValueOfType(Collection collection, Class[] types) throws IllegalArgumentException {
		if (isEmpty(collection) || ObjectUtils.isEmpty(types)) {
			return null;
		}
		for (Class type : types) {
			Object value = findValueOfType(collection, type);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	/**
	 * Convert the supplied array into a List. A primitive array gets converted into a List of the appropriate wrapper
	 * type.
	 * <p>
	 * A <code>null</code> source value will be converted to an empty List.
	 *
	 * @param source
	 *            the (potentially primitive) array
	 * @return the converted List result
	 * @see ObjectUtils#toObjectArray(Object)
	 */
	@SuppressWarnings("rawtypes")
	public static List arrayToList(Object source) {
		return Arrays.asList(ObjectUtils.toObjectArray(source));
	}

	/**
	 * Merge the given Properties instance into the given Map, copying all properties (key-value pairs) over.
	 * <p>
	 * Uses <code>Properties.propertyNames()</code> to even catch default properties linked into the original Properties
	 * instance.
	 *
	 * @param props
	 *            the Properties instance to merge (may be <code>null</code>)
	 * @param map
	 *            the target Map to merge the properties into
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void mergePropertiesIntoMap(Properties props, Map map) {
		if (map == null) {
			throw new IllegalArgumentException("Map must not be null");
		}
		if (props != null) {
			for (Enumeration en = props.propertyNames(); en.hasMoreElements();) {
				String key = (String) en.nextElement();
				map.put(key, props.getProperty(key));
			}
		}
	}

	/**
	 * Return <code>true</code> if any element in '<code>candidates</code>' is contained in '<code>source</code>';
	 * otherwise returns <code>false</code>.
	 */
	@SuppressWarnings("rawtypes")
	public static boolean containsAny(Collection source, Collection candidates) {
		if (isEmpty(source) || isEmpty(candidates)) {
			return false;
		}
		for (Iterator it = candidates.iterator(); it.hasNext();) {
			if (source.contains(it.next())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return the first element in '<code>candidates</code>' that is contained in '<code>source</code>'. If no element
	 * in '<code>candidates</code>' is present in '<code>source</code>' returns <code>null</code>. Iteration order is
	 * {@link Collection} implementation specific.
	 */
	@SuppressWarnings("rawtypes")
	public static Object findFirstMatch(Collection source, Collection candidates) {
		if (isEmpty(source) || isEmpty(candidates)) {
			return null;
		}
		for (Iterator it = candidates.iterator(); it.hasNext();) {
			Object candidate = it.next();
			if (source.contains(candidate)) {
				return candidate;
			}
		}
		return null;
	}

	public static <T> T getFirstValue(List<T> c){
		if (isEmpty(c) ) {
			return null;
		}
		return c.get(0);
	}

	/**
	 * 比较两个list中数据异同，形成 新增（src余出）、排除（dest余出）、 交集 三类list.
	 *
	 * @param destList
	 * @param srcList
	 * @return List &lt;List&lt;srcRemain&gt; 、List&lt;destRemain&gt;、List&lt;mixSame&gt; &gt;
	 */
	public static List<List<Long>> analysisArrayScope(List<Long> destList, List<Long> srcList) {
		Set<Long> compareSet = new HashSet<Long>();
		List<Long> ignoreList = new ArrayList<Long>();
		List<Long> newlyList = new ArrayList<Long>();

		List<List<Long>> entirList = new ArrayList<List<Long>>();

		if (srcList.isEmpty() || destList.isEmpty()) {
			entirList.add(srcList);
			entirList.add(destList);
			entirList.add(new ArrayList<Long>());
			return entirList;
		}

		for (Long destItem : destList) {
			compareSet.add(destItem);
		}
		for (Long srcItem : srcList) {
			if (compareSet.contains(srcItem)) {
				compareSet.remove(srcItem);
				ignoreList.add(srcItem);
			} else {
				newlyList.add(srcItem);
			}
		}
		List<Long> detachList = new ArrayList<Long>(compareSet);
		entirList.add(newlyList);
		entirList.add(detachList);
		entirList.add(ignoreList);
		return entirList;
	}

	public static void removeNull(Collection<? extends Object>c){
		if(null==c){
			return;
		}
		while(c.remove(null)){
		}
	}


	public static <T>void removeByItemProcesser(Collection<T>c,ItemProcesser<T> itemProcesser){
		if(null==c){
			return;
		}
		final Iterator<T> iter=c.iterator();
		while(iter.hasNext()){
			final T t=iter.next();
			final Object result=itemProcesser.process(c, t);
		    if(result!=null){
		    	iter.remove();
		    }
		}
	}

	@SuppressWarnings("unchecked")
	public static<T,W,H> Map<T, List<H>> group(List<W> list,MapItemProcess<W> itemProcesser){
		final Map<T, List<H>> map=new HashMap<T, List<H>>();

		if(!CollectionUtils.isEmpty(list)){
			for (W w : list) {
				final T key=(T) itemProcesser.getKey(w);
				if(!map.containsKey(key)){
					map.put(key, new ArrayList<H>());
				}
				map.get(key).add((H) itemProcesser.getValue(w));
			}
		}

		return map;
	}

	public static<T> String toString(Collection<T> c,CollectionItemToString<T> collectionItemToString,String separator){
         if(CollectionUtils.isEmpty(c)){
        	 return null;
         }
         final StringBuilder sb=new StringBuilder();
         for (T object : c) {
        	 final String v=collectionItemToString.toString(object);
        	 sb.append(separator).append(v);
         }
         return sb.substring(separator.length());
	}

	public static interface CollectionItemToString<T>{
		 String toString(T t);
	}

	public static interface ItemProcesser<T>{
		Object process(Collection<? extends Object> c, T item);
	}

	public static<T> Map<Object,Object> collectionToMap(Collection<T> c,MapItemProcess<T> mapItemProcess){
		return collectionToMap(c,mapItemProcess,null);
	}

	public static<T> Map<Object,Object> collectionToMap(Collection<T> c,MapItemProcess<T> mapItemProcess,Map<?,?> map){
		@SuppressWarnings("unchecked")
		final Map<Object,Object> innterMap=(Map<Object, Object>) (map==null?new HashMap<Object,Object>():map);
		if(c==null){
			return innterMap;
		}
		final Iterator<T> iter=c.iterator();
		while (iter.hasNext()) {
			final T obj=iter.next();
			final Object value=mapItemProcess.getValue(obj);
			final Object key=mapItemProcess.getKey(obj);
			innterMap.put(key, value);
		}
		return innterMap;
	}

	public static abstract class MapItemProcess<T>{
		 public abstract Object getKey(T t);

		 public Object getValue(T t){
			 return t;
		 }
	}

	public static <T>TreeSet<T> createTreeSet(Collection<T> c,Comparator<T> cp){
		final TreeSet<T> set=new TreeSet<T>(cp);
		set.addAll(c);
		return set;
	}

	public static boolean isEmpty(Object[] objs) {
		return objs==null||objs.length<1;
	}
}
