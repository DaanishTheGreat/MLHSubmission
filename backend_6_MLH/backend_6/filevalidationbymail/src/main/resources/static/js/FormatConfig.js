function addFieldRow() {
    const table = document.getElementById('fieldTable');
    const rowCount = table.rows.length;
    const row = table.insertRow(rowCount);

    const cell1 = row.insertCell(0);
    const element1 = document.createElement('input');
    element1.type = 'text';
    element1.name = 'fields[' + rowCount + '].fieldName';
    cell1.appendChild(element1);

    const cell2 = row.insertCell(1);
    const element2 = document.createElement('input');
    element2.type = 'text';
    element2.name = 'fields[' + rowCount + '].fieldType';
    cell2.appendChild(element2);
}
