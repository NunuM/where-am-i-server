/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package me.nunum.whereami.framework.persistence.activerecord;

import me.nunum.whereami.framework.domain.Identifiable;

/**
 * An interface to mark a class as an Active Record.
 * <p>
 * Active Records might have static finder methods or use a separated Finder
 * class.
 *
 * @author nuno
 */
public interface ActiveRecord<ID> extends Identifiable<ID> {

    /*
     * save the current object to the persistence store either by creating it or
     * updating it
     */
    void save();
}
