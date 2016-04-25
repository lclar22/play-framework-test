row_id = location.pathname.split('/')[2]

$ ->
  $.get "/productRequestsByInsumoUser/" + row_id, (rows) ->
    $.each rows, (index, row) ->
      date = $("<td>").text row.date
      veterinario = $("<td>").text row.veterinario
      storekeeper = $("<td>").text row.storekeeper
      status = $("<td>").text row.status
      detail = $("<td>").text row.detail
      links = $("<td>").html '<a href="/productRequestByInsumo_send/' + row.id + '" class="btn btn-primary">Enviar</a>' + '<a href="/productRequestByInsumo_accept/' + row.id + '" class="btn btn-primary">Aceptar</a>' + '<a href="/productRequestByInsumo_finish/' + row.id + '" class="btn btn-primary">Finalizar</a>' + '<a href="/productRequestByInsumo_update/' + row.id + '" class="btn btn-primary">Editar</a>' + '<a href="/productRequestByInsumo_remove/' + row.id + '" class="btn btn-danger">Eliminar</a>' + '<a href="/productRequestByInsumo_show/' + row.id + '" class="btn btn-info">Mostrar</a>'
      $("#rows").append $("<tr>").append(date).append(veterinario).append(storekeeper).append(status).append(detail).append(links)
