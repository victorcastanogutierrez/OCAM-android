package com.ocam.model;

import com.ocam.model.types.ActionType;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.Arrays;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Entidad para encapsular la información de las acciones
 * que tiene por realizar de manera local
 */
@Entity
public class PendingAction {

    /**
     * ID de la accion local
     */
    @Id
    private Long id;

    /**
     * Accion que va a ser realizada
     */
    @NotNull
    @Convert(converter = ActionTypeConverter.class, columnType = Integer.class)
    private ActionType actionType;

    /**
     * Parametros que recibe la accion
     */
    @Convert(converter = ParametroConverter.class, columnType = String.class)
    private List<String> parametros;

    @Generated(hash = 555500347)
    public PendingAction(Long id, @NotNull ActionType actionType, List<String> parametros) {
        this.id = id;
        this.actionType = actionType;
        this.parametros = parametros;
    }

    @Generated(hash = 1012560780)
    public PendingAction() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActionType getActionType() {
        return this.actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public List<String> getParametros() {
        return this.parametros;
    }

    public void setParametros(List<String> parametros) {
        this.parametros = parametros;
    }

    public static class ActionTypeConverter implements PropertyConverter<ActionType, Integer> {

        @Override
        public ActionType convertToEntityProperty(Integer databaseValue) {
            if (databaseValue == null) {
                return null;
            }
            return ActionType.valueOf(databaseValue);
        }

        @Override
        public Integer convertToDatabaseValue(ActionType entityProperty) {
            if (entityProperty == null) {
                return null;
            }
            return entityProperty.getActionId();
        }
    }

    public static class ParametroConverter implements PropertyConverter<List<String>, String> {

        @Override
        public List<String> convertToEntityProperty(String databaseValue) {
            if (databaseValue == null) {
                return null;
            }
            else {
                return Arrays.asList(databaseValue.split(","));
            }
        }

        @Override
        public String convertToDatabaseValue(List<String> entityProperty) {
            if(entityProperty == null){
                return null;
            }
            else {
                StringBuilder sb = new StringBuilder();
                for(String link : entityProperty){
                    sb.append(link);
                    sb.append(",");
                }
                return sb.toString();
            }
        }
    }
}
