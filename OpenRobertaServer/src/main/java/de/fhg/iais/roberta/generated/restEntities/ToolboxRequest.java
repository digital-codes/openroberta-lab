/*
 * This is a class GENERATED by the TransportGenerator maven plugin. DON'T MODIFY IT.
 * IF you modify it, your work may be lost: the class will be overwritten automatically
 * when the maven plugin is re-executed for any reasons.
 */
package de.fhg.iais.roberta.generated.restEntities;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * the request description for the /toolbox REST request<br><br>
 * Version: 1<br>
 * Datum: 2020-06-15
 */
public class ToolboxRequest extends BaseRequest {
    protected String name;
    protected String owner;
    
    /**
     * the request description for the /toolbox REST request
     */
    public static ToolboxRequest make() {
        return new ToolboxRequest();
    }
    
    /**
     * the request description for the /toolbox REST request
     */
    public static ToolboxRequest makeFromString(String jsonS) {
        try {
            JSONObject jsonO = new JSONObject(jsonS);
            return make(jsonO);
        } catch (JSONException e) {
            throw new RuntimeException("JSON parse error when parsing: " + jsonS, e);
        }
    }
    
    /**
     * the request description for the /toolbox REST request
     */
    public static ToolboxRequest makeFromProperties(String cmd,String name,String owner) {
        ToolboxRequest entity = new ToolboxRequest();
        entity.setCmd(cmd);
        entity.setName(name);
        entity.setOwner(owner);
        entity.immutable();
        return entity;
    }
    
    /**
     * the request description for the /toolbox REST request
     */
    public static ToolboxRequest make(JSONObject jsonO) {
        return make().merge(jsonO).immutable();
    }
    
    /**
     * merge the properties of a JSON-object into this bean. The bean must be "under construction".
     * The keys of the JSON-Object must be valid. The bean remains "under construction".<br>
     * Throws a runtime exception if inconsistencies are detected.
     */
    public ToolboxRequest merge(JSONObject jsonO) {
        try {
            for (String key : JSONObject.getNames(jsonO)) {
                if ("_version".equals(key)) {
                } else if ("cmd".equals(key)) {
                    setCmd(jsonO.optString(key));
                } else if ("name".equals(key)) {
                    setName(jsonO.getString(key));
                } else if ("owner".equals(key)) {
                    setOwner(jsonO.getString(key));
                } else {
                    throw new RuntimeException("JSON parse error. Found invalid key: " + key + " in " + jsonO);
                }
            }
            return this;
        } catch (Exception e) {
            throw new RuntimeException("JSON parse / casting error when parsing: " + jsonO, e);
        }
    }
    
    /**
     * moves a bean from state "under construction" to state "immutable".<br>
     * Checks whether all required fields are set. All lists are made immutable.<br>
     * Throws a runtime exception if inconsistencies are detected.
     */
    public ToolboxRequest immutable() {
        if (this.immutable) {
            return this;
        }
        this.immutable = true;
        return validate();
    }
    
    /**
     * Checks whether all required fields are set.<br>
     * Throws a runtime exception if inconsistencies are detected.
     */
    private ToolboxRequest validate() {
        String _message = null;
        if ( !this.immutable ) {
            _message = "ToolboxRequest-object is already immutable: " + toString();
        }
        if ( name == null) {
            _message = "required property name of ToolboxRequest-object is not set: " + toString();
        }
        if ( owner == null) {
            _message = "required property owner of ToolboxRequest-object is not set: " + toString();
        }
        if ( _message != null ) {
            this.immutable = false;
            throw new RuntimeException(_message);
        }
        return this;
    }
    
    /**
     * GET name. Object must be immutable. Never return null or an undefined/default value.
     */
    public String getName() {
        if (!this.immutable) {
            throw new RuntimeException("no name from an object under construction: " + toString());
        }
        return this.name;
    }
    
    /**
     * SET name. Object must be mutable.
     */
    public ToolboxRequest setName(String name) {
        if (this.immutable) {
            throw new RuntimeException("name assigned to an immutable object: " + toString());
        }
        this.name = name;
        return this;
    }
    
    /**
     * GET owner. Object must be immutable. Never return null or an undefined/default value.
     */
    public String getOwner() {
        if (!this.immutable) {
            throw new RuntimeException("no owner from an object under construction: " + toString());
        }
        return this.owner;
    }
    
    /**
     * SET owner. Object must be mutable.
     */
    public ToolboxRequest setOwner(String owner) {
        if (this.immutable) {
            throw new RuntimeException("owner assigned to an immutable object: " + toString());
        }
        this.owner = owner;
        return this;
    }
    
    /**
     * generates a JSON-object from an immutable bean.<br>
     * Throws a runtime exception if inconsistencies are detected.
     */
    public JSONObject toJson() {
        if (!this.immutable) {
            throw new RuntimeException("no JSON from an object under construction: " + toString());
        }
        JSONObject jsonO = new JSONObject();
        try {
            jsonO.put("_version", "1");
            if (this.cmd != null) {
                jsonO.put("cmd", this.cmd);
            }
            jsonO.put("name", this.name);
            jsonO.put("owner", this.owner);
        } catch (JSONException e) {
            throw new RuntimeException("JSON unparse error when unparsing: " + this, e);
        }
        return jsonO;
    }
    
    @Override
    public String toString() {
        return "ToolboxRequest [immutable=" + this.immutable + ", cmd=" + this.cmd + ", name=" + this.name + ", owner=" + this.owner + " ]";
    }
    @Override
    public int hashCode() {
        throw new RuntimeException("no hashCode from transport beans!");
    }
    
    @Override
    public boolean equals(Object obj) {
        throw new RuntimeException("no equals from transport beans!");
    }
    
}