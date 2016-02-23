$ ->
  $.get "/insumos", (insumos) ->
    $.each insumos, (index, insumo) ->
      nombre = $("<td>").text insumo.nombre
      costo = $("<td>").text insumo.costo
      porcentage = $("<td>").text insumo.porcentage
      descripcion = $("<td>").text insumo.descripcion
      unidad = $("<td>").text insumo.unidad
      $("#insumos").append $("<tr>").append(nombre).append(costo).append(porcentage).append(descripcion).append(unidad)
