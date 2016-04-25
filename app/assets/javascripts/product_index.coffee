$ ->
  $.get "/insumos", (rows) ->
    $.each rows, (index, row) ->
      nombre = $("<td>").text row.nombre
      costo = $("<td>").text row.costo
      porcentage = $("<td>").text row.porcentage
      descripcion = $("<td>").text row.descripcion
      unidad = $("<td>").text row.unidad
      currentAmount = $("<td>").text row.currentAmount
      links = $("<td>").html '<a href="/insumo_update/' + row.id + '" class="btn btn-primary">Editar</a>' + '<a href="/insumo_remove/' + row.id + '" class="btn btn-danger">Eliminar</a>' + '<a href="/insumo_show/' + row.id + '" class="btn btn-info">Mostrar</a>'
      $("#rows").append $("<tr>").append(nombre).append(costo).append(porcentage).append(descripcion).append(unidad).append(currentAmount).append(links)
