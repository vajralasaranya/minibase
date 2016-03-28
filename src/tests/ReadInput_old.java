package tests;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import global.AttrOperator;
import global.AttrType;
import global.TupleOrder;
import heap.FieldNumberOutOfBoundException;
import heap.InvalidTupleSizeException;
import heap.InvalidTypeException;
import heap.Tuple;
import iterator.CondExpr;
import iterator.FileScan;
import iterator.FldSpec;
import iterator.IEJoin2Tables1Predicate;
import iterator.IEJoin2Tables2Predicates;
import iterator.NestedLoopsJoins;
import iterator.RelSpec;
import iterator.Sort;

class data {
	public String stringData;
	public Integer intData;
	boolean isString = false;
	boolean isInteger = false;
}

public class ReadInput_old {
	public enum Predicate {
		singlePredicate, doublePredicate, unknown;
	}

	public ArrayList<Integer> readFile(String filepath) {
		String line = null;

		ArrayList<Integer> schema = new ArrayList<Integer>();
		ArrayList<ArrayList<data>> listOfLists = new ArrayList<ArrayList<data>>();
		try {
			FileReader fileReader = new FileReader(filepath);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			Boolean schemaFlag = true;
			while ((line = bufferedReader.readLine()) != null) {
				String[] dataFields = line.split(",");
				if (schemaFlag) {
					schemaFlag = false;
					for (int i = 0; i < dataFields.length; ++i) {
						if (dataFields[i].equals("attrInteger")) {
							schema.add(AttrType.attrInteger);
						} else if (dataFields[i].equals("attrString")) {
							schema.add(AttrType.attrString);
						} else if (dataFields[i].equals("attrNull")) {
							schema.add(AttrType.attrNull);
						}
					}
					System.out.println(schema.toString());
					break;
				} else {
					/*
					 * ArrayList<data> singleList = new ArrayList<data>();
					 * for(int i = 0; i < dataFields.length; ++i)
					 * {
					 * 
					 * }
					 * listOfLists.add(singleList);
					 */
				}
			}
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + filepath + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + filepath + "'");
			// Or we could just do this:
			// ex.printStackTrace();
		}
		return schema;
	}

	public static int getProjInfo(String[] filesToRead, String filename) {
		if (filesToRead.length == 1) {
			return 1;
		}
		if (filesToRead[0].equals(filename)) {
			return 1;
		}
		return 0;
	}

	public static void setTuple(Tuple t, AttrType s, String value, Integer pos)
			throws FieldNumberOutOfBoundException, IOException, InvalidTypeException, InvalidTupleSizeException {
		Integer attType = s.attrType;
		switch (attType) {
		case AttrType.attrInteger:
			t.setIntFld(pos, Integer.parseInt(value));
			break;
		case AttrType.attrString:
			t.setStrFld(pos, value);
			break;
		case AttrType.attrSymbol:
			t.setFloFld(pos, Float.parseFloat(value));
			break;
		}
	}

	public static Tuple[] generateData(String fileName, Integer pos1, Integer pos2)
			throws FieldNumberOutOfBoundException, IOException, InvalidTypeException, InvalidTupleSizeException {
		String line = null;
		AttrType[] Stypes;
		List<Tuple> queryList = new ArrayList<Tuple>();
		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			int counter = 0;
			int columnsCount = bufferedReader.readLine().trim().split(",").length + 4; // First four columns are for office use ;)
			Stypes = new AttrType[columnsCount];
			for (int i = 0; i < columnsCount; i++) {
				Stypes[i] = new AttrType(AttrType.attrInteger); // Assumption: All columns are of integer type
			}
			while ((line = bufferedReader.readLine()) != null) {
				String[] tupleData = line.split(",");
				Tuple t = new Tuple();
				t.setHdr((short) columnsCount, Stypes, null);
				t.setIntFld(1, counter++); // id column
				setTuple(t, Stypes[pos1], tupleData[pos1], 2); // condition one column
				setTuple(t, Stypes[pos2], tupleData[pos2], 3); // condition two column
				for (int i = 0; i < tupleData.length; i++) {
					setTuple(t, Stypes[i], tupleData[i], i + 3);
				}
				queryList.add(t);
			}
			// Always close files.
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + fileName + "'");
			// Or we could just do this:
			// ex.printStackTrace();
		}
		Tuple[] tupleArray = new Tuple[queryList.size()];
		return queryList.toArray(tupleArray);
	}

	public static void main(String args[])
			throws IOException, FieldNumberOutOfBoundException, IOException, InvalidTypeException, InvalidTupleSizeException {
		String line = null;
		String queryFilePath = "/home/rajesh/Dropbox/SaRaj/Study/sem 2/DBMI/phase 3/query_1a.txt";
		String sourceDirPath = "/tmp/";
		Integer question1b = 1;

		if (args.length == 3) {
			// Use command line args, else use the above default values
			// System.out.print("Query file complete path: ");
			queryFilePath = args[0].trim();
			// System.out.print("Is this Task 1B (1 or 0)? ");
			question1b = Integer.parseInt(args[1].trim());
			// System.out.print("Full directory path where all relations (input files) are present: ");
			sourceDirPath = args[2].trim();
			// Append a forward slash if not present
			if (sourceDirPath.charAt(sourceDirPath.length() - 1) != '/')
				sourceDirPath += "/";
		}

		List<String> queryList = new ArrayList<String>();
		try {
			FileReader fileReader = new FileReader(queryFilePath);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while ((line = bufferedReader.readLine()) != null) {
				queryList.add(line);
			}
			// Always close files.
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + queryFilePath + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + queryFilePath + "'");
			// Or we could just do this:
			// ex.printStackTrace();
		}

		System.out.println("Given Query: ");
		int listSize = queryList.size();
		for (int i = 0; i < listSize; ++i)
			System.out.println(queryList.get(i));
		System.out.println();

		String[] filesToRead = queryList.get(1).split(" ");

		// output fields + offsets
		String output = queryList.get(0);
		String[] data = output.split(" ");
		FldSpec[] proj_list = new FldSpec[data.length];

		for (int i = 0; i < data.length; i++) {
			// System.out.println(data[i]);
			String[] results = data[i].split("_");
			if (getProjInfo(filesToRead, results[0]) == 1) {
				proj_list[i] = new FldSpec(new RelSpec(RelSpec.outer), Integer.parseInt(results[1]));
			} else {
				proj_list[i] = new FldSpec(new RelSpec(RelSpec.innerRel), Integer.parseInt(results[1]));
			}
			// System.out.println(Arrays.toString(results));
		}
		// short[] Jsizes = new short[1];
		// Jsizes[0] = 30;

		// Predicate predicateType = Predicate.unknown;
		if (queryList.size() == 3) {
			// Single predicate query
			// predicateType = Predicate.singlePredicate;
			if (filesToRead.length == 2) {
				System.out.println("Running query2c 1 Predicate");

				// variable queryList is [1K_1 2K_1, 1K 2K, 1K_3 4 2K_5]
				int t1cond1Col = Integer.parseInt(queryList.get(2).split(" ")[0].trim().split("_")[1].trim()); // get 3 from 1K_3 4 2K_5
				int t2cond1Col = Integer.parseInt(queryList.get(2).split(" ")[2].trim().split("_")[1].trim()); // get 4 from 1K_3 4 2K_5
				int op1 = Integer.parseInt(queryList.get(2).split(" ")[1].trim()); // get 4 from 1K_3 4 2K_4

				// All condition column indices are zero based where as input is 1 based. So subtract 1 from t(i)cond1Col where i = {1, 2}
				Tuple[] T = generateData(sourceDirPath + filesToRead[0] + ".txt", --t1cond1Col, t1cond1Col); // last arg is unused
				Tuple[] T1 = generateData(sourceDirPath + filesToRead[1] + ".txt", --t2cond1Col, t2cond1Col); // last arg is unused

				// new IEJoin2Tables1Predicate(T, T1, op1).executeAndPrintResults();
				long start = System.currentTimeMillis();
				int c = new IEJoin2Tables1Predicate(T, T1, op1).run().size();
				long end = System.currentTimeMillis();
				System.out.format("Found %d tuples\nTime Taken: %d\n", c, end - start);
			} else if (filesToRead.length == 1) {
				System.out.println("Running query2a");
				// query_2a();
			}
		} else if (queryList.size() == 5) {
			if (filesToRead.length == 2) {
				if (question1b == 0) {
					System.out.println("Running query2c"); // query_2c();

					// variable queryList is [R_1 S_1, R S, R_3 2 S_4, AND, R_5 1 S_6]
					int t1cond1Col = Integer.parseInt(queryList.get(2).split(" ")[0].trim().split("_")[1].trim()); // get 3 from R_3 2 S_4
					int t2cond1Col = Integer.parseInt(queryList.get(2).split(" ")[2].trim().split("_")[1].trim()); // get 4 from R_3 2 S_4
					int t1cond2Col = Integer.parseInt(queryList.get(4).split(" ")[0].trim().split("_")[1].trim()); // get 5 from R_5 1 S_6
					int t2cond2Col = Integer.parseInt(queryList.get(4).split(" ")[2].trim().split("_")[1].trim()); // get 6 from R_5 1 S_6
					int op1 = Integer.parseInt(queryList.get(2).split(" ")[1].trim()); // get 2 from R_3 2 S_4
					int op2 = Integer.parseInt(queryList.get(4).split(" ")[1].trim()); // get 1 from R_5 1 S_6

					// All condition column indices are zero based where as input is 1 based. So subtract 1 from t(i)cond(i)Col where i = {1, 2}
					Tuple[] T = generateData(sourceDirPath + filesToRead[0] + ".txt", --t1cond1Col, --t1cond2Col);
					Tuple[] T1 = generateData(sourceDirPath + filesToRead[1] + ".txt", --t2cond1Col, --t2cond2Col);

					// new IEJoin2Tables2Predicates(T, T1, op1, op2).printResults();
					long start = System.currentTimeMillis();
					int c = new IEJoin2Tables2Predicates(T, T1, op1, op2).run().size();
					long end = System.currentTimeMillis();
					System.out.format("Found %d tuples\nTime Taken: %d\n", c, end - start);

				} else if (question1b == 1) {
					System.out.println("Running query1b");
					// query_1b();

					boolean status = true;

					CondExpr[] outFilter = new CondExpr[3];
					outFilter[0] = new CondExpr();
					outFilter[1] = new CondExpr();
					outFilter[2] = new CondExpr();

					outFilter[0].next = null;
					outFilter[0].op = new AttrOperator(AttrOperator.aopEQ);
					outFilter[0].type1 = new AttrType(AttrType.attrSymbol);
					outFilter[0].type2 = new AttrType(AttrType.attrSymbol);
					outFilter[0].operand1.symbol = new FldSpec(new RelSpec(RelSpec.outer), 1);
					outFilter[0].operand2.symbol = new FldSpec(new RelSpec(RelSpec.innerRel), 1);

					outFilter[1].op = new AttrOperator(AttrOperator.aopGE);
					outFilter[1].next = null;
					outFilter[1].type1 = new AttrType(AttrType.attrSymbol);
					outFilter[1].type2 = new AttrType(AttrType.attrInteger);
					outFilter[1].operand1.symbol = new FldSpec(new RelSpec(RelSpec.innerRel), 2);
					outFilter[1].operand2.integer = 1;
					outFilter[2] = null;

				}
			} else if (filesToRead.length == 1) {
				System.out.println("Running query2b");
				// query_2b();
			}
		} else {
			// unknown predicate query
			// predicateType = Predicate.unknown;
		}
		/*
		 * readInput ri = new readInput();
		 * ArrayList<Integer> schema = ri.readFile("/tmp/S.txt");
		 */
	}
}