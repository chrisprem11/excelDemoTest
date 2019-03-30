package com.example.demo.service;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;;

@Service
public class UploadService {

	private final UploadUtil uploadUtil;

	public UploadService(UploadUtil uploadUtil) {
		this.uploadUtil = uploadUtil;
	}

	public List<Map<String, String>> upload(Sheet sheet) throws Exception {

		Supplier<Stream<Row>> rowStreamSupplier = uploadUtil.getRowStreamSupplier(sheet);

		Row headerRow = rowStreamSupplier.get().findFirst().get();

//		List<String> headerCells = uploadUtil.getStream(headerRow).map(Cell::getNumericCellValue).map(String::valueOf)
//				.collect(Collectors.toList());

		List<String> headerCells = uploadUtil.getStream(headerRow).map(String::valueOf).collect(Collectors.toList());
		int colCount = headerCells.size();

		return rowStreamSupplier.get().skip(1).map(row -> {

			List<String> cellList = uploadUtil.getStream(row).map(String::valueOf).collect(Collectors.toList());

			return uploadUtil.cellIteratorSupplier(colCount).get().collect(toMap(headerCells::get, cellList::get));
		}).collect(Collectors.toList());
	}

}
